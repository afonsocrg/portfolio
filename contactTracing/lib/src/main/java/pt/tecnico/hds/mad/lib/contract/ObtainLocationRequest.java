package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class ObtainLocationRequest extends ContractMessage {
    private static final MessageType type = MessageType.OBTAIN_LOCATION_REQUEST;

    private UserLocationQuery userLocationQuery;

    public ObtainLocationRequest(UserLocationQuery userLocationQuery) {
        this.userLocationQuery = userLocationQuery;
    }

    public UserLocationQuery getUserLocationQuery() {
        return userLocationQuery;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());
        res.add("content", this.userLocationQuery.toJson());
        return res;
    }

    public static ObtainLocationRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            UserLocationQuery query = UserLocationQuery.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new ObtainLocationRequest(query);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
