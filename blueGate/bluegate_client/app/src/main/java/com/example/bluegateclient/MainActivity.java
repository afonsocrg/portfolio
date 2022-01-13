package com.example.bluegateclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    // request codes for result activities
    private static final int REQUEST_ENABLE_BT = 1;

    // App logic
    private UUID myUUID;
    private Map<String, PrivateKey> privateKeys;
    private Map<String, PublicKey> publicKeys;


    // App UI
    private LinearLayout list;

    // Bluetooth
    private BluetoothAdapter bluetoothAdapter = null;
    private int btState;
    private BluetoothDevice gate;

    // Testing
    private String lastMessage = null;

    // Security
    private SecureRandom secureRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        this.myUUID = UUID.fromString(Config.MY_UUID_STRING);
        this.list = findViewById(R.id.text_list);
        secureRandom = new SecureRandom();

        // load here every possible user
        try {
            this.privateKeys = new HashMap<>();
            this.publicKeys = new HashMap<>();

            this.privateKeys.put(Config.ADMIN, readPrivateKey(Config.ADMIN_PRIV));
            this.privateKeys.put(Config.ALICE, readPrivateKey(Config.ALICE_PRIV));
            this.privateKeys.put(Config.BOB, readPrivateKey(Config.BOB_PRIV));


            this.publicKeys.put(Config.ADMIN, readPublicKey(Config.ADMIN_PUB));
            this.publicKeys.put(Config.ALICE, readPublicKey(Config.ALICE_PUB));
            this.publicKeys.put(Config.BOB, readPublicKey(Config.BOB_PUB));

        } catch (GeneralSecurityException | IOException e) {
            fatal("Error while loading keys: " + e.getMessage());
        }

        if(BiometricManager.from(this).canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            log("FAILED BIOMETRICS!");
            fatal("The current device does not support biometric authentication");
        }

        if(!setupBluetooth()) {
            log("FAILED BLUETOOTH!");
            fatal("The current device does not support bluetooth communication");
        }

        IntentFilter stateChangedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter FoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(this.btStateReceiver, stateChangedFilter);
        registerReceiver(this.deviceFoundReceiver, FoundFilter);

    }

    // listens for incoming broadcast messages
    private final BroadcastReceiver btStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                MainActivity.this.btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if(MainActivity.this.btState == BluetoothAdapter.STATE_ON) {
                    pairBluetooth(); // try to pair to gate
                }
            }
        }
    };

    private final BroadcastReceiver deviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (foundDevice.getAddress().equals(Config.GATE_MAC_ADDRESS)) {
                    log("Gate found during bluetooth discovery");
                    MainActivity.this.gate = foundDevice;
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister broadcast listeners
        unregisterReceiver(this.btStateReceiver);
        unregisterReceiver(this.deviceFoundReceiver);
    }


    // ====================================
    // Event handlers
    // ====================================
    public void handleOpenAlice(View view) {
        genericHandler("Scan your fingerprint to open the gate", () -> {
            sendOpenRequest(Config.ALICE);
        });
    }

    public void handleOpenBob(View view) {
        genericHandler("Scan your fingerprint to open the gate", () -> {
            sendOpenRequest(Config.BOB);
        });
    }

    public void handleRegister(View view) {
        genericHandler("Scan your fingerprint to register a new user", () -> {
            sendRegisterRequest(Config.BOB);
        });
    }

    public void handleReplay(View view) {
        replayAttack();
    }


    private interface CallBack {
        void go();
    }

    // Every time we want to handle a button, we want to do this
    private void genericHandler(String message, CallBack cb) {
        if(this.gate == null) {
            warning("Failed to connect to gate. Retrying...");
            pairBluetooth();
            return;
        }

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(message)
                .setNegativeButtonText("Cancel")
                .build();

        //This will give result of the authentication
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        cb.go();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                }
        );
        biometricPrompt.authenticate(promptInfo);
    }

    // ====================================
    // IO
    // ====================================
    private void sendOpenRequest(String user) {
        try {
            String request = createRequest(user, "open", new HashMap<>());
            String response = sendRequest(request);
            log("Got Response: " + response);
        } catch (IOException e) {
            log("Error while communicating with gate: " + e.getMessage());
        }
    }

    private void sendRegisterRequest(String user) {
        PublicKey key = this.publicKeys.get(user);
        Map<String, String> args = new HashMap<>();
        args.put("user", user);
        args.put("key", tob64(key.getEncoded()));

        try {
            String request = createRequest(Config.ADMIN, "register", args);
            String response = sendRequest(request);
            log("Got Response: " + response);
        } catch (IOException e) {
            log("Error while communicating with gate: " + e.getMessage());
        }
    }

    private void replayAttack() {
        if(this.lastMessage == null) {
            warning("No message to replay");
            return;
        }
        try {
            String request = this.lastMessage;
            String response = sendRequest(request);
            log("Got Response: " + response);
        } catch (IOException e) {
            log("Error while communicating with gate: " + e.getMessage());
        }
    }



    private String createRequest(String user, String command, Map<String, String> args) throws IOException {
        // The JSON objects are created
        JsonObject request = new JsonObject();

        JsonObject content = createContent(user, command, args);
        String nonce = createSecureNonce();
        long timestamp = System.currentTimeMillis();

        // Building the JSON block
        request.add("content", content);
        request.addProperty("nonce", nonce);
        request.addProperty("timestamp", timestamp);

        String signature = getRequestSignature(request, this.privateKeys.get(user));
        request.addProperty("signature",signature);

        return request.toString();
    }

    private JsonObject createContent(String user, String command, Map<String, String> args) {
        JsonObject request = new JsonObject();
        request.addProperty("from", user);
        request.addProperty("command", command);

        JsonObject arguments = new JsonObject();
        for(Map.Entry<String, String> entry : args.entrySet()) {
            arguments.addProperty(entry.getKey(), entry.getValue());
        }

        request.add("arguments", arguments);
        return request;
    }

    private String getRequestSignature(JsonObject request, PrivateKey privateKey) throws IOException {
        try {
            // Generating the signature of the message and adding it to the message
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] byteHash = messageDigest.digest(request.toString().getBytes());


            // Encrypt the hash with our private key to make a signature
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] byteSignature = rsaCipher.doFinal(byteHash);
            // This call requires android API 26 or higher.
            return tob64(byteSignature);

        } catch (IllegalBlockSizeException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | BadPaddingException e) {
            // Simplifying the possible exceptions. If this is
            // well coded, these will never be thrown
            throw new IOException(e.getMessage());
        }
    }

    private String createSecureNonce(){
        byte[] byteNonce = new byte[64];
        secureRandom.nextBytes(byteNonce);
        return tob64(byteNonce);
    }


    // Assumes every byte is ascii printable
    private String readMessage(InputStream inputStream) throws IOException {
        StringBuilder res = new StringBuilder();

        while (true) {
            byte b = (byte) inputStream.read();
            if (b == -1) throw new IOException();
            if (b == 0) break; // stop reading on \x00
            if (b < 0x20 || 0x7e < b) continue; // ignore non printable characters (???)
            res.append((char)b);
        }

        return res.toString();
    }

    private void sendMessage(String msg, OutputStream outputStream) throws IOException {
        this.lastMessage = msg;
        byte[] bytes = msg.getBytes();
        outputStream.write(bytes);
        outputStream.write(0);
        outputStream.flush();
    }

    public PrivateKey readPrivateKey(String privateKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privEncoded = readFile(privateKeyPath);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);
        return priv;
    }

    public PublicKey readPublicKey(String publicKeyPath) throws IOException, GeneralSecurityException {
        byte[] pubEncoded = readFile(publicKeyPath);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);
        return pub;
    }

    private byte[] readFile(String path) throws IOException {
        InputStream stream = getAssets().open(path);

        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();
        return buffer;
    }

    private String tob64(byte[] msg) {
        return Base64.getEncoder().encodeToString(msg);
    }


    // ====================================
    // Bluetooth
    // ====================================
    // https://developer.android.com/guide/topics/connectivity/bluetooth#SettingUp

    private boolean setupBluetooth() {
        // 1. Get Bluetooth adapter
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(this.bluetoothAdapter == null) return false;

        this.btState = this.bluetoothAdapter.getState();
        pairBluetooth();
        return true;
    }

    private void enableBluetooth() {
        log("Enabling bluetooth");
        // 2. Check if Bluetooth is enabled
        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void pairBluetooth() {
        log("Pairing bluetooth");
        if(this.btState != BluetoothAdapter.STATE_ON) {
            enableBluetooth();
            return;
        }

        // 3. Check if we are already paired with the gate or try to pair with it
        Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().equals(Config.GATE_MAC_ADDRESS)) {
                log("Gate found as paired device");
                this.gate = device;
                return;
            }
        }

        // If we do not already have the device paired we start looking for it
        log("Gate not paired yet, starting bluetooth discovery");
        this.bluetoothAdapter.startDiscovery();
    }

    private BluetoothSocket connectBluetooth(BluetoothDevice device) throws IOException {
        // 5. We can now initialise our connection to the gate
        // We use an insecure RFCOMM socket to be able to add our own protection on top of it

        log("Creating Bluetooth socket");
        BluetoothSocket bluetoothSocket;
        try {
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            throw new IOException("Failed to create bluetooth socket: " + e.getMessage());
        }

        log("Connecting to gate");
        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                throw new IOException("Failed to close the client socket: " + closeException.toString());
            }
            throw new IOException("Could not connect to the host socket: " + e.toString());
        }
        log("Connection successful, starting communication");
        return bluetoothSocket;
    }

    private String sendRequest(String request) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BluetoothSocket bluetoothSocket = connectBluetooth(this.gate);
        inputStream = bluetoothSocket.getInputStream();
        outputStream = bluetoothSocket.getOutputStream();

        sendMessage(request, outputStream);
        String response = readMessage(inputStream);

        inputStream.close();
        outputStream.close();
        return response;
    }

    // ====================================
    // Utility
    // ====================================
    private void fatal(String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("The application will now exit.")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    log("Exiting application");
                    System.exit(-1);
                })
                .show();
    }

    private void warning(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {})
                .show();
    }

    private void log(String msg) {
        System.out.println("[DBG] " + msg);
        appendMessage("[DBG] " + msg);
    }

    private void clearList() {
        this.list.removeAllViews();
    }

    private void appendMessage(String msg) {
        TextView textView = new TextView(this);
        textView.setText(msg);
        textView.setGravity(Gravity.START);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        list.addView(textView);
    }
}