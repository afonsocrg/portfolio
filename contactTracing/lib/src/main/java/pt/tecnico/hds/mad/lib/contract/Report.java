package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class Report implements ContractObject {
    private Record record;
    private List<Proof> proofs;

    public Report(Record record, List<Proof> proofs) {
        this.record = record;
        this.proofs = proofs;
    }

    public Record getRecord() { return this.record; }
    public String getUserId() { return this.record.getUserId(); }
    public int getEpoch() { return this.record.getEpoch(); }
    public List<Proof> getProofs() { return this.proofs; }

    public int getNumProofs() { return this.proofs.size(); }
    public void removeProof(Proof p) {
        this.proofs.remove(p);
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        JsonArray proofArray = new JsonArray();
        for(Proof p : this.proofs) {
            proofArray.add(p.toJson());
        }

        res.add("record", this.record.toJson());
        res.add("proofs", proofArray);
        return res;
    }

    public static Report fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            Record record = Record.fromJson(json.remove("record").getAsJsonObject());

            List<Proof> proofs = new LinkedList();
            JsonArray jsonProofs = json.remove("proofs").getAsJsonArray();
            Iterator<JsonElement> it = jsonProofs.iterator();
            while(it.hasNext()) {
                proofs.add(Proof.fromJson(it.next().getAsJsonObject()));
            }

            // if remaining elements, json was not a report
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new Report(record, proofs);
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
        Report other = (Report) o;

        return this.getRecord().equals(other.getRecord()) && 
            this.getProofs().containsAll(other.getProofs()) &&
            other.getProofs().containsAll(this.getProofs());

    }
}
