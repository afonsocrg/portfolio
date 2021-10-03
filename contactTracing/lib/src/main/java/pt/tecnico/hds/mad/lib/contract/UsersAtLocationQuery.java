package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.Objects;

public class UsersAtLocationQuery implements ContractObject{
    private int x;
    private int y;
    private int epoch;

    public UsersAtLocationQuery(int x, int y, int epoch) {
        this.x = x;
        this.y = y;
        this.epoch = epoch;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getEpoch() {
        return epoch;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();
        req.addProperty("x", this.x);
        req.addProperty("y", this.y);
        req.addProperty("epoch", this.epoch);
        return req;
    }

    public static UsersAtLocationQuery fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            int x = json.remove("x").getAsInt();
            int y = json.remove("y").getAsInt();
            int epoch = json.remove("epoch").getAsInt();

            // if remaining elements, json was not a UsersAtLocationQuery
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new UsersAtLocationQuery(x, y, epoch);
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
        UsersAtLocationQuery that = (UsersAtLocationQuery) o;
        return getX() == that.getX() && getY() == that.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getEpoch());
    }
}
