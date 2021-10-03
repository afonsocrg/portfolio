package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.*;

public class UsersAtLocationReport implements ContractObject{

    private Map<String, Proof> usersAndProofs;

    public UsersAtLocationReport() {
        this.usersAndProofs = new HashMap<>();
    }

    public UsersAtLocationReport(Map<String, Proof> usersAndProofs) {
        this.usersAndProofs = usersAndProofs;
    }

    public void addUser(String userId, Proof proof) {
        usersAndProofs.put(userId, proof);
    }

    public Map<String, Proof> getUsersAndProofs() {
        return usersAndProofs;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();
        JsonObject usersProofs = new JsonObject();
        this.usersAndProofs.forEach((k,v) -> usersProofs.add(k, v.toJson()));
        req.add("users", usersProofs);
        return req;
    }

    public static UsersAtLocationReport fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            JsonObject users = json.remove("users").getAsJsonObject();
            // if remaining elements, json was not a UsersAtLocationReport
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            Map<String, Proof> usersList = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry: users.entrySet()) {
                usersList.put(entry.getKey(), Proof.fromJson(entry.getValue().getAsJsonObject()));
            }

            return new UsersAtLocationReport(usersList);
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
        UsersAtLocationReport that = (UsersAtLocationReport) o;
        return getUsersAndProofs().equals(that.getUsersAndProofs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsersAndProofs());
    }
}
