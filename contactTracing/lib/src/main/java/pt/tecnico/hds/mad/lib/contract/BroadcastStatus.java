package pt.tecnico.hds.mad.lib.contract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;

// Represents the possible response status from the server
public enum BroadcastStatus {
    SEND("SEND"),
    ECHO("ECHO"),
    READY("READY");

    private final String label;

    // reverse lookup table. Allows to return enum value from its string
    private static final Map<String, BroadcastStatus> revTable = new HashMap<>();
    static {
        // build reverse lookup table
        Arrays.asList(values())
            .forEach( v -> revTable.put(v.label, v));
    }

    BroadcastStatus(String label) { this.label = label; }
    public String getLabel() { return this.label; }
    public static BroadcastStatus fromLabel(String label) throws InvalidEnumLabelException {
        BroadcastStatus res = revTable.get(label);
        if(res == null) {
            throw new InvalidEnumLabelException();
        }
        return res;
    }
}


