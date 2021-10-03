package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.contract.Record;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class LocationProofRequest extends ContractMessage {
    private static final MessageType type = MessageType.LOCATION_PROOF_REQUEST;
    private Record record;

    public LocationProofRequest(Record record) {
        this.record = record;
    }

    public Record getRecord() { return this.record; }

    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());
        res.add("content", this.record.toJson());
        return res;
    }

    public static LocationProofRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            Record record = Record.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new LocationProofRequest(record);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
