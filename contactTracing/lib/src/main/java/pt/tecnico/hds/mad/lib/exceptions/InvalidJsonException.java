package pt.tecnico.hds.mad.lib.exceptions;

public class InvalidJsonException extends Exception {
    public final static String INVALID_FORMAT = "Json format does not follow protocol";

    public InvalidJsonException(String m) {
        super(m);
    }
}
