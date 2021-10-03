package pt.tecnico.hds.mad.lib.exceptions;

public class InvalidEnumLabelException extends Exception {
    public InvalidEnumLabelException() {
        super("Given label does not correspond to enum value");
    }
}
