package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;

public abstract class ContractMessage {

    public static MessageType getMessageType(JsonObject msg) throws InvalidJsonException {
        try {
            String type = msg.get("type").getAsString();
            return MessageType.fromLabel(type);
        } catch (NullPointerException | InvalidEnumLabelException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }

    public abstract JsonObject toJson();
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractMessage msg = (ContractMessage) o;
        return msg.toString().equals(this.toString());
    }
}
