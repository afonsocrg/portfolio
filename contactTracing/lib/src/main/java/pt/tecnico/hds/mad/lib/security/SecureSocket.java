package pt.tecnico.hds.mad.lib.security;

import java.io.*;
import java.security.*;
import java.net.Socket;
import java.util.UUID;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;

import com.google.gson.*;

public class SecureSocket implements AutoCloseable {
     private final int DEFAULT_POW_ZERO_BIT_LENGTH = 15;

    private Socket s;

    private PublicKey localPublicKey;
    private PrivateKey localPrivateKey;
    private PublicKey remotePublicKey;

    private String remoteId;

    private boolean isCiphering;

    // private NonceChecker nonceChecker;

    public SecureSocket(Socket s, PublicKey localPublicKey, PrivateKey localPrivateKey,
            String remoteId, PublicKey remotePublicKey, boolean isCiphering) {
        this.s = s;

        this.localPublicKey = localPublicKey;
        this.localPrivateKey = localPrivateKey;
        this.remotePublicKey = remotePublicKey;

        this.remoteId = remoteId;

        this.isCiphering = isCiphering;

        // this.nonceChecker = new NonceChecker();
    }

    public SecureSocket(Socket s, PublicKey localPublicKey, PrivateKey localPrivateKey,
            String remoteId, PublicKey remotePublicKey) {
        // by default, secure sockets cipher communication
        this(s, localPublicKey, localPrivateKey, remoteId, remotePublicKey, true);
    }

    public String getRemoteId() { return this.remoteId; }

    /* Identifying isn't done right away on socket creation
       because of our use of insecure connections
     */
    public void identify(String id) throws IdentityException {
        try {
            this.write(new IdentityRequest(id).toJson().toString());

            JsonObject json = JsonParser.parseString(this.read()).getAsJsonObject();
            IdentityResponse response = IdentityResponse.fromJson(json);
            if (!response.isOk()) {
                throw new IdentityException("Reason: " + response.getReason());
            }

        } catch (IOException e) {
            throw new IdentityException("IO: " + e.getMessage());
        } catch (InvalidJsonException e) {
            throw new IdentityException("JSON: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        this.s.close();
    }

    public void sendExpensive(JsonObject content) throws GeneralSecurityException, IOException, ProofOfWorkException {
        try {
            this.send(content);
            ProofOfWorkRequest powRequest = ProofOfWorkRequest.fromJson(this.receive());

            String id = powRequest.getId();
            int cost = powRequest.getCost();

            // System.out.println(powRequest.toString());

            String powTarget = content.toString() + id;
            String powResponse = SecurityUtils.genPowResponse(powTarget, cost);

            // System.out.printf("Generated PoW Response: %s%n", powResponse);

            ProofOfWorkRequesterResponse requesterResponse = new
                    ProofOfWorkRequesterResponse(powResponse);
            this.send(requesterResponse.toJson());

            ProofOfWorkProviderResponse providerResponse =
                    ProofOfWorkProviderResponse.fromJson(this.receive());

            if (!providerResponse.isOk()) {
                throw new ProofOfWorkException(providerResponse.getReason());
            }
        } catch (InvalidJsonException e) {
            throw new ProofOfWorkException("JSON: " + e.getMessage());
        }
    }

    public void receiveExpensive(JsonObject request) throws IOException, GeneralSecurityException, ProofOfWorkException {
        this.receiveExpensive(request, DEFAULT_POW_ZERO_BIT_LENGTH);
    }

    public void receiveExpensive(JsonObject request, int cost) throws IOException, GeneralSecurityException, ProofOfWorkException {
        String id = UUID.randomUUID().toString();
        String powTarget = request.toString() + id;

        ProofOfWorkRequest powRequest = new ProofOfWorkRequest(id, cost);
        this.send(powRequest.toJson());

        try {
            String requesterResponse = ProofOfWorkRequesterResponse.fromJson(this.receive()).getResponse();
            // System.out.printf("Received Pow response: %s%n", requesterResponse);

            if (SecurityUtils.verifyPowResponse(powTarget, cost, requesterResponse)) {
                this.send(ProofOfWorkProviderResponse.Ok().toJson());
            } else {
                String msg = "Invalid proof of work";
                this.send(ProofOfWorkProviderResponse.Nok(msg).toJson());
                throw new ProofOfWorkException(msg);
            }
        } catch (InvalidJsonException e) {
            String msg = "JSON: " + e.getMessage();
            this.send(ProofOfWorkProviderResponse.Nok(msg).toJson());
            throw new ProofOfWorkException(msg);
        }
    }

    public void send(JsonObject content) throws IOException, GeneralSecurityException {
        JsonObject envelope = wrapContent(content);
        // System.out.println("[+] Sending: " + envelope);
        this.write(envelope.toString());
    }

    public boolean hasBytes() throws IOException {
        return this.s.getInputStream().available() > 0;
    }
    public JsonObject receive() throws IOException, GeneralSecurityException {
        String text = read();
        // System.out.println("Received: " + text);
        return this.unwrapContent(text);
    }


    /*
     * {
     *   signature: b64(sign({nonce, plain_request})) (inserted after signing. remove before checking)
     *   nonce: <nonce>
     *   key: b64(rsa(sym_key, receiver_pubKey))
     *   request: b64(aes(<request>, sym_key))
     * }
     */
    public JsonObject wrapContent(JsonObject content) throws GeneralSecurityException {
        JsonObject envelope = new JsonObject();

        // Freshness: Nonce
        // envelope.addProperty("nonce", SecurityUtils.newNonce());

        // Signature: sign plain request
        envelope.add("request", content);
        String b64sig = SecurityUtils.sign(envelope.toString(), this.localPrivateKey);
        envelope.addProperty("signature", b64sig);

        // Confidentiality: Hybrid Cipher (AES + RSA)
        if(this.isCiphering) {

            // Make sure we use correct provider
            Security.insertProviderAt(new BouncyCastleProvider(), 1);

            // Generate IV
            byte[] iv = SecurityUtils.newIv();

            // Generate and cipher random AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES", "BC");
            keyGen.init(256);
            Key aes_key = keyGen.generateKey();
            byte[] ciphered_aes_key = SecurityUtils.rsa(
                 Cipher.ENCRYPT_MODE,
                 aes_key.getEncoded(),
                 this.remotePublicKey
            );

            // Cipher request
            byte[] requestBytes = content.toString().getBytes();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aes_key, new IvParameterSpec(iv));
            byte[] cipheredRequest = cipher.doFinal(requestBytes);

            // Add info to envelope
            envelope.addProperty("iv", SecurityUtils.tob64(iv));
            envelope.addProperty("key", SecurityUtils.tob64(ciphered_aes_key));
            envelope.addProperty("request", SecurityUtils.tob64(cipheredRequest));
        }

        return envelope;
    }


    /* 
     * When receiving a message, the socket will decipher a message
     * if it is ciphered. The message is accepted even if it is not ciphered
     * (if it meets the integrity constraints)
     */
    public JsonObject unwrapContent(String text) throws GeneralSecurityException {
        JsonObject envelope = null;
        try {
            envelope = JsonParser.parseString(text).getAsJsonObject();

            // Decipher request if needed
            JsonElement b64KeyJson = envelope.remove("key");
            if(b64KeyJson != null) {
                // get cipher fields
                String b64Key = b64KeyJson.getAsString();
                String iv = envelope.remove("iv").getAsString();
                String b64Request = envelope.remove("request").getAsString();

                // get key
                byte[] cipheredKey = SecurityUtils.fromb64(b64Key);
                byte[] aesKey_bytes = SecurityUtils.rsa(Cipher.DECRYPT_MODE, cipheredKey, this.localPrivateKey);
                Key aes_key = new SecretKeySpec(aesKey_bytes, 0, aesKey_bytes.length, "AES");
          
                // decipher request
                byte[] ciphered_request = SecurityUtils.fromb64(b64Request);
                byte[] ivBytes = SecurityUtils.fromb64(iv);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, aes_key, new IvParameterSpec(ivBytes));
                byte[] deciphered_request_bytes = cipher.doFinal(ciphered_request);
                String requestText = new String(deciphered_request_bytes);

                // place plain request in envelope (check integrity)
                // need to parse request to Json, so gson does not escape the inner double quotes
                // inside the string. If it does, the signature check will fail
                JsonObject requestJson = JsonParser.parseString(requestText).getAsJsonObject();
                envelope.add("request", requestJson);
            }

            // Verify freshness
            // String nonce = envelope.get("nonce").getAsString();
            // if(!this.nonceChecker.check(nonce)) {
                // throw new SecurityException(SecurityException.NONCE_FAILED);
            // }

            // Verify integrity
            String b64sig = envelope.remove("signature").getAsString();
            if(!SecurityUtils.checkSignature(envelope.toString(), b64sig, this.remotePublicKey)) {
                throw new GeneralSecurityException(SecurityExceptionMessages.SIGNATURE_FAILED);
            }

        } catch (NullPointerException | JsonParseException e) {
            throw new GeneralSecurityException(InvalidJsonException.INVALID_FORMAT);
        }
        return envelope.getAsJsonObject("request");
    }

    private String read() throws IOException {
        StringBuilder res = new StringBuilder();
        InputStream in = this.s.getInputStream();
        while(true) {
            byte b = (byte)in.read();
            if (b == -1) throw new IOException();
            if (b == 0) break; // stop reading from input on \x00
            if (b < 0x20 || 0x7e < b) continue; // ignore non printable characters (???)
            res.append((char)b);
        }
        return res.toString();
    }

    private void write(String msg) throws IOException {
        OutputStream out = this.s.getOutputStream();
        out.write(msg.getBytes());
        out.write(0);
        out.flush();
    }
}
