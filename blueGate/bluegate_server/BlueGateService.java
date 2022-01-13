import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

import javax.crypto.Cipher;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Dictionary;
import java.util.HashMap;

import com.google.gson.*;

public class BlueGateService extends BluetoothService {

    /**
     * The gate this service controls
     */
    private GateController gate;

    private SecureBluetooth secureBt;


    public BlueGateService(String uuid, String name) throws BluetoothException {
        super(uuid, name);
        try {
            this.gate = new GateController();
        } catch (IOException e) {
            throw new BluetoothException("Could not create gate controller");
        }

        try {
            this.secureBt = new SecureBluetooth();
        } catch (IOException | ClassNotFoundException e) {
            throw new BluetoothException("Could not initialize SecureBluetooth: " + e.getMessage());
        }

    }

    private void open_gate() {
        try {
            this.gate.open();
        } catch (IOException e) {
            System.err.println("IO error while opening door");
        }
    }

    @Override
    public void handle_client(StreamConnection connection) throws IOException {
        RemoteDevice remoteDevice;
        InputStream in;
        OutputStream out;
        String client_name;

        remoteDevice = RemoteDevice.getRemoteDevice(connection);
        in = connection.openInputStream();
        out = connection.openOutputStream();
        client_name = remoteDevice.getFriendlyName(false);

        System.out.println("New client: " + client_name);

        // String msg = read_message(in);

        JsonObject msg;
        try {
            msg = this.secureBt.secureRead(in);
        } catch (BtSecurityException e) {
            System.out.println("Received corrupted message: " + e.getMessage());
            in.close();
            out.close();
            return;
        }

        // handle client command
        JsonObject content = msg.getAsJsonObject("content");
        String from = content.get("from").getAsString();
        String command = content.get("command").getAsString();
        JsonObject args = content.getAsJsonObject("arguments");

        System.out.println("Received " + command + " from " + from);
        switch(command) {
            case "open":
                if(this.secureBt.checkPermission(from, command)) {
                    System.out.println("Access granted!");
                    this.secureBt.secureSend(out, "Permission granted");
                    open_gate();
                } else {
                    System.out.println("Access denied!");
                    this.secureBt.secureSend(out, "Permission denied");
                }
                break;
            case "register":
                if(this.secureBt.checkPermission(from, command)) {
                    String user = args.get("user").getAsString();
                    String b64key = args.get("key").getAsString();
                    if(this.secureBt.register(user, b64key)) {
                        this.secureBt.secureSend(out, "Successfully registered " + user);
                    } else {
                        this.secureBt.secureSend(out, "Failed to register " + user);
                    }
                } else {
                    this.secureBt.secureSend(out, "Permission denied");
                }

                break;
            default:
                System.out.println("Unkown command!");
                System.out.println(content.toString());
                break;
        }

        in.close();
        out.close();
    }
}
