package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.contract.*;

public class BroadcastMessage extends ContractMessage {
    private static final MessageType type = MessageType.BROADCAST_MESSAGE;

    private BroadcastStatus status;
    private JsonObject message;

    public BroadcastMessage(JsonObject message, BroadcastStatus status) {
        this.message = message;
        this.status = status;
    }

    public BroadcastStatus getStatus() { return this.status; }
    public JsonObject getMessage() { return this.message; }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());

        res.add("message", this.message);
        res.addProperty("status", this.status.toString());
        return res;
    }

    public static BroadcastMessage fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            JsonObject message = json.remove("message").getAsJsonObject();

            String statusLabel = json.remove("status").getAsString();
            BroadcastStatus status = BroadcastStatus.fromLabel(statusLabel);


            if (json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new BroadcastMessage(message, status);
        } catch(InvalidEnumLabelException | NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
