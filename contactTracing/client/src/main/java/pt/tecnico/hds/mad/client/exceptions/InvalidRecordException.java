package pt.tecnico.hds.mad.client.exceptions;

public class InvalidRecordException extends Exception {
    public static final String INVALID_POSITION = "Invalid position";
    public static final String INVALID_EPOCH = "Invalid epoch";
    public static final String INVALID_USER = "Invalid user";
    public static final String TOO_FAR = "Requesting user is too far away";

    public InvalidRecordException(String m) { super(m); }

}
