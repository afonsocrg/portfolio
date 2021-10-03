package pt.tecnico.hds.mad.lib.exceptions;

public class KeyPoolException extends Exception {
    public static final String KEYSTORE_LOAD_FAILED = "Unable to load keystore";
    public static final String KEY_LOAD_FAILED = "Unable to load key";

    private String keyId;

    public KeyPoolException(String msg) { super(msg); }

    public KeyPoolException(String msg, String keyId) {
        super(msg);
        this.keyId = keyId;
    }

    public String getKeyId() {
        return this.keyId;
    }
}
