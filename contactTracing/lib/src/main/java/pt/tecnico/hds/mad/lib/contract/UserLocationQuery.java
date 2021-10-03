package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.Objects;

public class UserLocationQuery implements ContractObject {
    private String userId;
    private int epoch;

    public UserLocationQuery(String userId, int epoch) {
        this.userId = userId;
        this.epoch = epoch;
    }

    public int getEpoch() {
        return epoch;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();
        req.addProperty("user_id", this.userId);
        req.addProperty("epoch", this.epoch);
        return req;
    }

    public static UserLocationQuery fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String userId = json.remove("user_id").getAsString();
            int epoch = json.remove("epoch").getAsInt();

            // if remaining elements, json was not a UserLocationQuery
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new UserLocationQuery(userId, epoch);
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
        UserLocationQuery that = (UserLocationQuery) o;
        return getUserId().equals(that.getUserId()) && getEpoch() == that.getEpoch();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getEpoch());
    }
}
