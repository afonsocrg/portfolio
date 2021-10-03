package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.contract.Report;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class SubmitLocationRequest extends ContractMessage {
    private static final MessageType type = MessageType.SUBMIT_LOCATION_REQUEST;
    private Report report;

    public SubmitLocationRequest(Report report) {
        this.report = report;
    }

    public Report getReport() { return this.report; }

    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());
        res.add("content", this.report.toJson());
        return res;
    }

    public static SubmitLocationRequest fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            Report report = Report.fromJson(json.remove("content").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new SubmitLocationRequest(report);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
