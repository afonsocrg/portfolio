package pt.tecnico.hds.mad.lib.security;

import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;

import java.io.File;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPool {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private KeyStore keyStore;

    public KeyPool(String keyStorePath, String storePass, String localId, String localPass) throws KeyPoolException {
        try {
            this.keyStore = KeyStore.getInstance(new File(keyStorePath), storePass.toCharArray());
        } catch (Exception e) {
            throw new KeyPoolException(KeyPoolException.KEYSTORE_LOAD_FAILED);
        }

        try {
            this.privateKey = (PrivateKey) this.keyStore.getKey(localId, localPass.toCharArray());
            this.publicKey = getPublicKey(localId);
        } catch (Exception e) {
            throw new KeyPoolException(KeyPoolException.KEY_LOAD_FAILED);
        }
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey(String id) throws KeyPoolException {
        try {
            return this.keyStore.getCertificate(id).getPublicKey();
        } catch (Exception e) {
            throw new KeyPoolException(KeyPoolException.KEY_LOAD_FAILED, id);
        }
    }
}
