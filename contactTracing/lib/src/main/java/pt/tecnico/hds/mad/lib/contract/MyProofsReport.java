package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyProofsReport implements ContractObject {
    private List<RecordProofPair> proofs;

    public MyProofsReport(List<RecordProofPair> proofs) {
        this.proofs = proofs;
    }

    public List<RecordProofPair> getProofs() {
        return proofs;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();
        JsonArray arr = new JsonArray();
        this.proofs.forEach(x -> arr.add(x.toJson()));
        req.add("proofs", arr);
        return req;
    }

    public static MyProofsReport fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            JsonArray proofs = json.remove("proofs").getAsJsonArray();
            // if remaining elements, json was not a UsersAtLocationReport
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            List<RecordProofPair> proofsList = new ArrayList<>();

            for (JsonElement el: proofs) {
                proofsList.add(RecordProofPair.fromJson(el.getAsJsonObject()));
            }

            return new MyProofsReport(proofsList);
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
        MyProofsReport that = (MyProofsReport) o;
        return this.proofs.equals(that.getProofs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProofs());
    }
}
