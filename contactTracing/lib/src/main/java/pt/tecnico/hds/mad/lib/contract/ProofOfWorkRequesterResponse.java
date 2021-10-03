package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class ProofOfWorkRequesterResponse extends ContractMessage {
    private static final MessageType type = MessageType.PROOF_OF_WORK_REQUESTER_RESPONSE;
    private String response;

    public ProofOfWorkRequesterResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return this.response;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());

        JsonObject powContent = new JsonObject();
        powContent.addProperty("response", this.response);
        res.add("content", powContent);
        return res;
    }

    public static ProofOfWorkRequesterResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            JsonObject content = json.remove("content").getAsJsonObject();
            String response = content.remove("response").getAsString();

            if (json.size() > 0 || content.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new ProofOfWorkRequesterResponse(response);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
