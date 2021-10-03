package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class UsersAtLocationRequest extends ContractMessage {
    private static final MessageType type = MessageType.USERS_AT_LOCATION_REQUEST;

    private UsersAtLocationQuery usersAtLocationQuery;

    public UsersAtLocationRequest(UsersAtLocationQuery usersAtLocationQuery) {
        this.usersAtLocationQuery = usersAtLocationQuery;
    }

    public UsersAtLocationQuery getUsersAtLocationQuery() {
        return usersAtLocationQuery;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());
        res.add("content", this.usersAtLocationQuery.toJson());
        return res;
    }

    public static UsersAtLocationRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            UsersAtLocationQuery query = UsersAtLocationQuery.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new UsersAtLocationRequest(query);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
