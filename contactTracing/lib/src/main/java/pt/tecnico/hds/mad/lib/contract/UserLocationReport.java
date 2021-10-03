package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.Objects;

public class UserLocationReport implements ContractObject{
    private Report report;

    public UserLocationReport(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();
        req.add("report", this.report.toJson());
        return req;
    }

    public static UserLocationReport fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            Report report = Report.fromJson(json.remove("report").getAsJsonObject());

            // if remaining elements, json was not a UserLocationReport
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new UserLocationReport(report);
        } catch (NullPointerException
                | ClassCastException
                | IllegalStateException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLocationReport that = (UserLocationReport) o;
        return getReport().equals(that.getReport());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReport());
    }

    @Override
    public String toString() {
        return "(" + this.getReport().getRecord().getX() + ", " + this.getReport().getRecord().getY() + ")";
    }
}
