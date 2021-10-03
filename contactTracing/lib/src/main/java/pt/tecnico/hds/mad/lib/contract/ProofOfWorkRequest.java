package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class ProofOfWorkRequest extends ContractMessage {
    private static final MessageType type = MessageType.PROOF_OF_WORK_REQUEST;
    private String id;
    private int cost;

    public ProofOfWorkRequest(String id, int cost) {
        this.id = id;
        this.cost = cost;
    }

    public String getId() {
        return this.id;
    }

    public int getCost() {
        return this.cost;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());

        JsonObject powContent = new JsonObject();
        powContent.addProperty("id", this.id);
        powContent.addProperty("cost", this.cost);
        res.add("content", powContent);
        return res;
    }

    public static ProofOfWorkRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            JsonObject content = json.remove("content").getAsJsonObject();
            String id = content.remove("id").getAsString();
            int cost = content.remove("cost").getAsInt();

            if (json.size() > 0 || content.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new ProofOfWorkRequest(id, cost);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
