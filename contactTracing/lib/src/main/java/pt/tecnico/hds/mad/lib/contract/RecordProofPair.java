package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.Objects;

public class RecordProofPair implements ContractObject {
    private Record record; // We need to send the record as well so that the user can verify if it is a correct signature
    private Proof proof;
    
    public RecordProofPair(Record record, Proof proof) {
        this.record = record;
        this.proof = proof;
    }

    public Proof getProof() {
        return proof;
    }

    public Record getRecord() {
        return record;
    }

    @Override
    public JsonObject toJson() {
        JsonObject req = new JsonObject();
        req.add("record", this.record.toJson());
        req.add("proof", this.proof.toJson());
        return req;
    }

    public static RecordProofPair fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();

            Record record = Record.fromJson(json.remove("record").getAsJsonObject());
            Proof proof = Proof.fromJson(json.remove("proof").getAsJsonObject());

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new RecordProofPair(record, proof);
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
        RecordProofPair that = (RecordProofPair) o;
        return this.record.equals(that.getRecord()) && this.proof.equals(that.getProof());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecord(), getProof());
    }
}
