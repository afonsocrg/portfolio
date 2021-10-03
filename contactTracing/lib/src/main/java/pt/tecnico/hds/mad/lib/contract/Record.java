package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import java.util.Objects;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class Record implements ContractObject {
    private String userId;
    private int epoch;
    private int x;
    private int y;

    public Record(String userId, int epoch, int x, int y) {
        this.userId = userId;
        this.epoch = epoch;
        this.x = x;
        this.y = y;
    }

    public String getUserId() {
        return this.userId;
    }

    public int getEpoch() {
        return this.epoch;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("user_id", this.userId);
        res.addProperty("epoch", this.epoch);
        res.addProperty("x", this.x);
        res.addProperty("y", this.y);
        return res;
    }

    public static Record fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String userId = json.remove("user_id").getAsString();
            int epoch = json.remove("epoch").getAsInt();
            int x = json.remove("x").getAsInt();
            int y = json.remove("y").getAsInt();

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new Record(userId, epoch, x, y);
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
        Record record = (Record) o;
        return getUserId().equals(record.getUserId()) && getEpoch() == record.getEpoch() && getX() == record.getX() && getY() == record.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getEpoch(), getX(), getY());
    }
}
