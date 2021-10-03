package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidEnumLabelException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class MyProofsResponse extends ContractMessage {
    private static final MessageType type = MessageType.MY_PROOFS_RESPONSE;
    private ResponseStatus status;

    private MyProofsReport myProofsReport;

    public MyProofsResponse(ResponseStatus status, MyProofsReport myProofsReport) {
        this.status = status;
        this.myProofsReport = myProofsReport;
    }

    public MyProofsReport getMyProofsReport() {
        return myProofsReport;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", type.getLabel());
        res.addProperty("status", this.status.getLabel());
        if (this.myProofsReport != null)
            res.add("content", this.myProofsReport.toJson());
        return res;
    }

    public static MyProofsResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            String label = json.remove("status").getAsString();
            ResponseStatus status = ResponseStatus.fromLabel(label);
            MyProofsReport report = null;

            if (status.getLabel().equals(ResponseStatus.OK.getLabel()))
                report = MyProofsReport.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new MyProofsResponse(status, report);
        } catch (NullPointerException | InvalidEnumLabelException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
