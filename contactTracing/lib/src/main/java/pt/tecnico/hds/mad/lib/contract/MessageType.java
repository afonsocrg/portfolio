package pt.tecnico.hds.mad.lib.contract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;

// Represents the possible message types (client to server and vice versa)
public enum MessageType {
    BROADCAST_MESSAGE("Broadcast message"),

    SUBMIT_LOCATION_REQUEST("Submit Location Request"),
    SUBMIT_LOCATION_RESPONSE("Submit Location Response"),
    OBTAIN_LOCATION_REQUEST("Obtain Location Request"),
    OBTAIN_LOCATION_RESPONSE("Obtain Location Response"),

    LOCATION_PROOF_REQUEST("Location Proof Request"),
    LOCATION_PROOF_RESPONSE("Location Proof Response"),

    USERS_AT_LOCATION_REQUEST("Users At Location Request"),
    USERS_AT_LOCATION_RESPONSE("Users At Location Response"),

    MY_PROOFS_REQUEST("My Proofs Request"),
    MY_PROOFS_RESPONSE("My Proofs Response"),

    IDENTITY_REQUEST("Identity Request"),
    IDENTITY_RESPONSE("Identity Response"),

    PROOF_OF_WORK_REQUEST("Proof of Work Request"),
    PROOF_OF_WORK_REQUESTER_RESPONSE("Proof of Work Requester Response"),
    PROOF_OF_WORK_PROVIDER_RESPONSE("Proof of Work Provider Response"),

    ERROR_MESSAGE("ERROR");

    private final String label;

    // reverse lookup table. Allows to return enum value from its string
    private static final Map<String, MessageType> revTable = new HashMap<>();
    static {
        // build reverse lookup table
        Arrays.asList(values())
            .forEach( v -> revTable.put(v.label, v));
    }

    MessageType(String label) { this.label = label; }
    public String getLabel() { return this.label; }
    public static MessageType fromLabel(String label) throws InvalidEnumLabelException {
        MessageType res = revTable.get(label);
        if(res == null) {
            throw new InvalidEnumLabelException();
        }
        return res;
    }
}
