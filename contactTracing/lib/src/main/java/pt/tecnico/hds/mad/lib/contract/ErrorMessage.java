package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.contract.Report;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

// Differs from a specific result since it doesn't respond directly to a request
// This type of message just informs that there was an error during the processing
// of the request
public class ErrorMessage extends ContractMessage {
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String INVALID_REQUEST_FORMAT = "Invalid request format";
    public static final String UNKNOWN_REQUEST = "Invalid request: Unknown request";
    public static final String SECURITY_ERROR = "Security Error";

    private static final MessageType type = MessageType.ERROR_MESSAGE;

    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());
        res.addProperty("error", this.error);
        return res;
    }

    public static ErrorMessage fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            String error  = json.remove("error").getAsString();

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new ErrorMessage(error);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
