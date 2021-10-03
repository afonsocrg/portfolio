package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidObtainLocationResponseException;

public class ObtainLocationResponse extends ContractMessage {
    private static final MessageType type = MessageType.OBTAIN_LOCATION_RESPONSE;

    private ResponseStatus status;

    private UserLocationReport userLocationReport;

    public ObtainLocationResponse(ResponseStatus status, UserLocationReport userLocationReport) throws InvalidObtainLocationResponseException {
        this.status = status;

        if(status.getLabel().equals(ResponseStatus.OK.getLabel()) && userLocationReport == null ||
                (status.getLabel().equals(ResponseStatus.INVALID_ID_REQUEST.getLabel()) || status.getLabel().equals(ResponseStatus.NO_INFORMATION_FOR_QUERY.getLabel())) &&
                        userLocationReport != null) {
            throw new InvalidObtainLocationResponseException();

        }

        this.userLocationReport = userLocationReport;
    }
    public ResponseStatus getStatus() {
        return status;
    }

    public UserLocationReport getUserLocationReport() {
        return userLocationReport;
    }

    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());
        res.addProperty("status", this.status.getLabel());
        if (userLocationReport != null)
            res.add("content", this.userLocationReport.toJson());
        return res;
    }

    public static ObtainLocationResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            String label = json.remove("status").getAsString();
            ResponseStatus status = ResponseStatus.fromLabel(label);
            UserLocationReport userLocationReport = null;

            if (status.getLabel().equals(ResponseStatus.OK.getLabel()))
                userLocationReport = UserLocationReport.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new ObtainLocationResponse(status, userLocationReport);
        } catch (NullPointerException | InvalidObtainLocationResponseException | InvalidEnumLabelException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
