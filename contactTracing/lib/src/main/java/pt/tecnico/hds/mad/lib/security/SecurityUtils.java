package pt.tecnico.hds.mad.lib.security;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

import com.google.gson.*;

public class SecurityUtils {

    private static final String DIGEST_ALGO = "SHA-256";

    public static String newNonce() {
        byte[] res = new byte[64];
        new SecureRandom().nextBytes(res);
        return tob64(res);
    }

    public static byte[] newIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // return b64 signature
    public static String sign(String m, PrivateKey k) throws GeneralSecurityException {
        byte[] digest = digest(m);
        byte[] signature = rsa(Cipher.ENCRYPT_MODE, digest, k);
        String b64sig = tob64(signature);
        // System.out.println("[+] Got signature: " + b64sig);
        return b64sig;
    }

    public static boolean checkSignature(String msg, String b64sig, PublicKey key)
            throws GeneralSecurityException {

        byte[] sig = fromb64(b64sig);
        byte[] bad_digest = digest(msg);
        byte[] gud_digest = rsa(Cipher.DECRYPT_MODE, sig, key);
        return Arrays.equals(bad_digest, gud_digest);
    }

    private static byte[] digest(String m) throws GeneralSecurityException {
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        return messageDigest.digest(m.getBytes());
    }

    public static byte[] rsa(int opmode, byte[] m, Key k) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(opmode, k);
        return cipher.doFinal(m);
    }

    public static String genPowResponse(String target, int cost) {
        String response;
        do {
            response = UUID.randomUUID().toString();
        } while (!verifyPowResponse(target, cost, response));

        return response;
    }

    public static boolean verifyPowResponse(String target, int cost, String response) {
        try {
            byte[] bytes = digest(target + response);
            BitSet bits = BitSet.valueOf(bytes);

            for (int i = 0; i < cost; i++) {
                if (bits.get(i)) { return false; }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String tob64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    public static byte[] fromb64(String s) throws GeneralSecurityException {
        try {
            return Base64.getDecoder().decode(s);
        } catch(IllegalArgumentException e) {
            throw new GeneralSecurityException(e);
        }
    }

    public static PrivateKey readPrivateKey(String privateKeyPath) throws IOException, GeneralSecurityException {
        byte[] privEncoded = readFile(privateKeyPath);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFacPriv = KeyFactory.getInstance("RSA");
        return keyFacPriv.generatePrivate(privSpec);
    }

    public static PublicKey readPublicKey(String publicKeyPath) throws IOException, GeneralSecurityException {
        byte[] pubEncoded = readFile(publicKeyPath);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
        return keyFacPub.generatePublic(pubSpec);
    }

    private static byte[] readFile(String path) throws IOException {
        byte[] content = null;
        FileInputStream fis = new FileInputStream(path);
        content = new byte[fis.available()];
        fis.read(content);
        fis.close();
        return content;
    }
}
