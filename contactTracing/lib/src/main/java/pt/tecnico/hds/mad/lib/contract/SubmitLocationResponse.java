package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.contract.Report;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;

public class SubmitLocationResponse extends ContractMessage {
    private static final MessageType type = MessageType.SUBMIT_LOCATION_RESPONSE;

    private ResponseStatus status;

    public SubmitLocationResponse(ResponseStatus status) {
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());
        res.addProperty("status", this.status.getLabel());
        return res;
    }

    public static SubmitLocationResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            String label = json.remove("status").getAsString();
            ResponseStatus status = ResponseStatus.fromLabel(label);

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new SubmitLocationResponse(status);
        } catch (NullPointerException | InvalidEnumLabelException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }

}
