package pt.tecnico.hds.mad.lib.contract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;

// Represents the possible response status from the server
public enum ResponseStatus {
    OK("OK"),
    INVALID_ID_REQUEST("Invalid id request"),
    INSUFFICIENT_EVIDENCE("Insufficient Evidence"),
    NO_INFORMATION_FOR_QUERY("No information for this query");

    private final String label;

    // reverse lookup table. Allows to return enum value from its string
    private static final Map<String, ResponseStatus> revTable = new HashMap<>();
    static {
        // build reverse lookup table
        Arrays.asList(values())
            .forEach( v -> revTable.put(v.label, v));
    }

    public String getLabel() { return this.label; }
    ResponseStatus(String label) { this.label = label; }
    public static ResponseStatus fromLabel(String label) throws InvalidEnumLabelException {
        ResponseStatus res = revTable.get(label);
        if(res == null) {
            throw new InvalidEnumLabelException();
        }
        return res;
    }
}


