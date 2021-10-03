package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class MyProofsRequest extends ContractMessage {
    private static final MessageType type = MessageType.MY_PROOFS_REQUEST;

    private MyProofsQuery myProofsQuery;

    public MyProofsRequest(MyProofsQuery myProofsQuery) {
        this.myProofsQuery = myProofsQuery;
    }

    public MyProofsQuery getMyProofsQuery() {
        return myProofsQuery;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());
        res.add("content", this.myProofsQuery.toJson());
        return res;
    }

    public static MyProofsRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            MyProofsQuery query = MyProofsQuery.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new MyProofsRequest(query);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
