import java.io.FileInputStream;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public class SecureBluetooth {

    private static String DIGEST_ALGO = "SHA-256";
    private static String  RSA_CIPHER = "RSA/ECB/PKCS1Padding";

    private static String KNOWN_PUB_KEYS = "keys/known_pkeys.keys";
    private static String PERMISSIONS = "config/permissions.json";

    private static long TS_FRESHNESS_LIMIT = 10000; // (ms)

    private ArrayList<String> previousNonces;
    private Map<String, PublicKey> knownPublicKeys;     // { user: publicKey }
    private Map<String, Set<String>> permissions;       // { operation: < allowed users > }

    public SecureBluetooth() throws IOException, ClassNotFoundException {
        this.previousNonces = new ArrayList<String>();
        this.knownPublicKeys = new HashMap<String, PublicKey>();

        // Set up known public keys
        File f = new File(KNOWN_PUB_KEYS);
        if(!f.exists() || f.isDirectory()) { // system is fresh
            bootstrapKnownKeys();

        } else { // Already had allowed public keys
            this.knownPublicKeys = loadKnownKeys();
            for(String key : this.knownPublicKeys.keySet()) {
                System.out.println("Loaded key for " + key);
            }
        }

        // Set up permissions
        try {
            this.permissions = loadPermissions(PERMISSIONS);
        } catch (IOException e) {
            throw new IOException("Error while reading permission file: " + e.getMessage());
        }
    }



    /**
     * Checks if a certain user is allowed to perform some operation
     * @param operation The operation to be performed
     * @param user The user
     * @return whether or not the user is allowed to perform the operation
     */
    public boolean checkPermission(String user, String operation) {
        if(!this.permissions.containsKey(operation)) {
            return false;
        }

        return this.permissions.get(operation).contains(user);
    }


    /*
     * Message format: {
     *   signature: <base64 signature>, // signs the whole message, including nonce and timestamp
     *   nonce: 
     *   timestamp: 
     *   content: { ... }
     * }
     */
    public JsonObject secureRead (InputStream in) throws IOException, BtSecurityException {
        String msg = readMessage(in);
        System.out.println("Received: " + msg);

        // will be later used when checking timestamp. We want to get the closest gap possible here
        Long currentTime = System.currentTimeMillis();

        JsonObject jsonMsg;
        try {
            jsonMsg = JsonParser.parseString(msg).getAsJsonObject();
        } catch (JsonParseException e) {
            throw new BtSecurityException("Invalid JSON");
        }

        // Get message author's public key
        JsonObject jsonContent = jsonMsg.get("content").getAsJsonObject();
        if(jsonContent == null) {
            throw new BtSecurityException("Invalid JSON");
        }

        String alleged_author = jsonContent.get("from").getAsString();
        if(alleged_author == null) {
            throw new BtSecurityException("Invalid JSON");
        }

        PublicKey alleged_pubKey = this.knownPublicKeys.get(alleged_author);
        if(alleged_pubKey == null) {
            throw new BtSecurityException("Unknown sender");
        }



        // Get the signature and remove it from the message
        // (removing it from message to get the message that was signed)
        JsonElement sig_element = jsonMsg.remove("signature");
        if(sig_element == null) {
            throw new BtSecurityException("Invalid JSON: No signature field found");
        }
        String alleged_b64sig = sig_element.getAsString();

        try {
            if(!checkSignature(jsonMsg.toString(), alleged_b64sig, alleged_pubKey)) {
                System.out.println("Signature failed!");
                throw new BtSecurityException("Invalid signature");
            }
        } catch (NoSuchAlgorithmException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchPaddingException e ) {
            e.printStackTrace();
            throw new BtSecurityException("Error while checking signature");
        }

        // Check that the nonce has not been used before
        // TODO: remove from msg
        String nonce = jsonMsg.get("nonce").getAsString();
        if(this.previousNonces.contains(nonce)) {
            throw new BtSecurityException("Duplicate message");
        } else {
            this.previousNonces.add(nonce);
        }

        // Check that the timestamp is fresh and has not been used before
        long timestamp = jsonMsg.get("timestamp").getAsLong();
        if(timestamp < currentTime - TS_FRESHNESS_LIMIT) {
            throw new BtSecurityException("Invalid timestamp");
        }


        return jsonMsg;
    }

    public void secureSend(OutputStream out, String msg) throws IOException {
        sendMessage(out, msg);
    }

    /**
     * Checks if the given message was signed by the owner of the given pubic key
     * @param msg The message to check
     * @param b64sig The received signature (base64 encoded)
     * @param key The public key of the signer
     * @return Wether if the signature is valid or not
     */
    private static boolean checkSignature(String msg, String b64sig, PublicKey key)
        throws NoSuchAlgorithmException, IllegalBlockSizeException,
               BadPaddingException, InvalidKeyException, NoSuchPaddingException
    {
        byte[] msg_digest = digest(msg);
        byte[] sig = fromb64(b64sig);
        byte[] decrypted_digest = rsa(Cipher.DECRYPT_MODE, sig, key);
        return Arrays.equals(msg_digest, decrypted_digest);
    }

    /**
     * Digests a message using the class digest algorithm
     * @param msg The message to digest
     * @return The digest bytes
     */
    private static byte[] digest(String msg)
        throws NoSuchAlgorithmException
    {
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        return messageDigest.digest(msg.getBytes());
    }

    /**
     * Applies given RSA transformation (encrypt or decrypt) to msg using given key
     * @param opmode Encrypt or Decrypt (use Cipher.DECRYPT_MODE / Cipher.ENCRYPT_MODE)
     * @param msg The message to transform
     * @param key The key to use in the transformation
     */
    private static byte[] rsa(int opmode, byte[] msg, Key key)
        throws NoSuchAlgorithmException, IllegalBlockSizeException,
               BadPaddingException, InvalidKeyException, NoSuchPaddingException
    {
        Cipher cipher = Cipher.getInstance(RSA_CIPHER);
        cipher.init(opmode, key);
        return cipher.doFinal(msg);
    }

    /**
     * Encodes bytes to b64
     * @param b The bytes to encode
     * @return The resulting encoded string
     */
    private static String tob64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    /**
     * Decodes b64 string
     * @param s The b64 string to decode
     * @return The resulting byte array
     */
    private static byte[] fromb64(String s) {
        return Base64.getDecoder().decode(s);
    }


    /**
     * Read permission JSON file. Assumes the file is well formatted
     * @param Path to read from
     * @return The corresponding permission object
     */
    private Map<String, Set<String>> loadPermissions(String permissionsFile) throws IOException {
        InputStream in = new FileInputStream(permissionsFile);
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

        Map<String, Set<String>> perms = new HashMap<String, Set<String>>();
        reader.beginObject();
        while(reader.hasNext()) {
            String operation = reader.nextName();
            Set<String> allowed = new HashSet<String>();
            reader.beginArray();
            while(reader.hasNext()) {
                String user = reader.nextString();
                allowed.add(user);
            }
            reader.endArray();
            perms.put(operation, allowed);
        }
        reader.endObject();
        reader.close();
        return perms;
    }

    /**
     * Initializes known public keys data structure
     * Saves initialized key in approppriate directory for future usage
     */
    private void bootstrapKnownKeys() throws IOException {
        // Initial default keys
        Map<String, String> initialKeys = new HashMap<String, String>();
        // initialKeys.put("EmilNjor", "keys/emil.pub");
        initialKeys.put("Admin", "keys/admin_pub.key");
        initialKeys.put("Alice", "keys/alice_pub.key");
        // initialKeys.put("Bob", "keys/bob_pub.key"); // bob initially doesn't belong to the users

        for(Map.Entry<String, String> entry : initialKeys.entrySet()) {
            try {
                PublicKey key = readPublicKey(entry.getValue());
                this.knownPublicKeys.put(entry.getKey(), key);
                System.out.println("Read key from " + entry.getKey());
            } catch (GeneralSecurityException
                    | IOException e) {
                throw new IOException("Could not read public key from file " + entry.getValue());
            }
        }

        saveKnownKeys();
    }

    /**
     * Loads saved known public keys. Returns the read object
     */
    private Map<String, PublicKey> loadKnownKeys() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(KNOWN_PUB_KEYS)
        );

        @SuppressWarnings("unchecked")
        Map<String, PublicKey> res = (HashMap) ois.readObject();
        ois.close();

        return res;
    }

    /**
     * Registers a new known user and its public key
     * If the user already exists, it is not updated
     * @param user The username to register
     * @param b64key The key (base64 encoded)
     * @return whether or not the registration was successfull
     */
    public boolean register(String user, String b64key) {

        // if duplicate user -> reject
        if(this.knownPublicKeys.containsKey(user)) {
            System.out.println("Failed to register user: User already exists");
            return false;
        }

        try {
            PublicKey key = publicKeyFromBytes(fromb64(b64key));
            this.knownPublicKeys.put(user, key);
            // saveKnownKeys();
        } catch(GeneralSecurityException
                // | IOException // temporarily disabled
                e) {
            System.out.println("Failed to register user: " + e.getMessage());
            return false;
        }

        // allow user to open door (temporarily)
        this.permissions.get("open").add(user);

        System.out.println("Successfully registered user " + user);
        return true;
    }


    /**
     * Saves known public keys
     */
    private void saveKnownKeys() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(KNOWN_PUB_KEYS)
        );
        oos.writeObject(this.knownPublicKeys);
        oos.close();
    }

    /**
     * read public key from specified path
     * @param The path to read from
     * @return The read PublicKey
     */
    private static PublicKey readPublicKey(String publicKeyPath)
        throws GeneralSecurityException, IOException
    {
        byte[] pubEncoded = readFile(publicKeyPath);
        return publicKeyFromBytes(pubEncoded);
    }

    /**
     * converts public key bytes to PublicKey object
     * @param public key bytes
     * @return PublicKey object
     */
    private static PublicKey publicKeyFromBytes(byte[] key)
        throws GeneralSecurityException
    {
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(key);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        PublicKey pub = keyFacPub.generatePublic(pubSpec);
        return pub;
    }

    /**
     * read private key from specified path
     * @param The path to read from
     * @return The read PrivateKey
     */
    private static PrivateKey readPrivateKey(String privateKeyPath)
        throws NoSuchAlgorithmException, InvalidKeySpecException,
               FileNotFoundException, IOException
    {
        byte[] privEncoded = readFile(privateKeyPath);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        PrivateKey priv = keyFacPriv.generatePrivate(privSpec);
        return priv;
	}

    /**
     * returns the bytes resulting from reading the file in the given path
     * @param path The path to read from
     * @return The read bytes
     */
	private static byte[] readFile(String path)
        throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        fis.read(content);
        fis.close();
        return content;
    }

    /**
     * reads a message from the given input stream (stops at \x00)
     * Ignores non printable characters
     * @param in The InputStream to read from
     * @return the read String
     */
    private String readMessage(InputStream in) throws IOException {
        StringBuilder res = new StringBuilder();
        while (true) {
            byte b = (byte) in.read();
            if (b == -1) throw new IOException();
            if (b == 0) break; // stop reading from input on \x00
            if (b < 0x20 || 0x7e < b) continue; // ignore non printable characters (???)
            res.append((char)b);
        }
        return res.toString();
    }


    /**
     * writes a string to the given OutputStream
     * @param out The output stream to write in
     * @param msg The message to write
     */
    private void sendMessage(OutputStream out, String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        out.write(bytes);
        out.write(0); // terminating null byte
        out.flush();
    }
}
