package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class UsersAtLocationResponse extends ContractMessage {
    private static final MessageType type = MessageType.USERS_AT_LOCATION_RESPONSE;
    private ResponseStatus status;
    
    private UsersAtLocationReport usersAtLocationReport;

    public UsersAtLocationResponse(ResponseStatus status, UsersAtLocationReport usersAtLocationReport) {
        this.status = status;
        this.usersAtLocationReport = usersAtLocationReport;
    }

    public UsersAtLocationReport getUsersAtLocationReport() {
        return usersAtLocationReport;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());
        res.addProperty("status", this.status.getLabel());
        if (usersAtLocationReport != null)
            res.add("content", this.usersAtLocationReport.toJson());
        return res;
    }

    public static UsersAtLocationResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            String label = json.remove("status").getAsString();
            ResponseStatus status = ResponseStatus.fromLabel(label);

            UsersAtLocationReport response = null;
            if (status.getLabel().equals(ResponseStatus.OK.getLabel()))
                 response = UsersAtLocationReport.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new UsersAtLocationResponse(status, response);
        } catch (NullPointerException | InvalidEnumLabelException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
