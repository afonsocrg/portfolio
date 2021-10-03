package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyProofsQuery implements ContractObject {
    private String userId;
    private List<Integer> epochs;

    public MyProofsQuery(String userId, List<Integer> epochs) {
        this.userId = userId;
        this.epochs = epochs;
    }

    public List<Integer> getEpochs() {
        return epochs;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();

        req.addProperty("user_id", this.userId);

        JsonArray ids = new JsonArray();
        this.epochs.forEach(ids::add);
        req.add("epochs", ids);
        return req;
    }

    public static MyProofsQuery fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();

            String userId = json.remove("user_id").getAsString();

            JsonArray epochsJson = json.remove("epochs").getAsJsonArray();
            // if remaining elements, json was not a MyProofsQuery
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            List<Integer> epochs = new ArrayList<>();
            epochsJson.forEach(x -> epochs.add(x.getAsInt()));

            return new MyProofsQuery(userId, epochs);
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
        MyProofsQuery that = (MyProofsQuery) o;
        return this.getUserId().equals(that.getUserId()) && this.epochs.equals(that.getEpochs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getEpochs());
    }
}
