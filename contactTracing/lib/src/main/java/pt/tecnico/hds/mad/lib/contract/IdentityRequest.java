package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class IdentityRequest extends ContractMessage {

    private static final MessageType type = MessageType.IDENTITY_REQUEST;
    private String id;

    public IdentityRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());

        JsonObject idContent = new JsonObject();
        idContent.addProperty("id", this.id);
        res.add("content", idContent);
        return res;
    }

    public static IdentityRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            JsonObject content = json.remove("content").getAsJsonObject();
            String id = content.remove("id").getAsString();

            if (json.size() > 0 || content.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new IdentityRequest(id);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
