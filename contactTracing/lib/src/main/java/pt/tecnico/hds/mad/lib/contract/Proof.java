package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import java.security.PublicKey;
import java.security.GeneralSecurityException;
import java.io.IOException;
import pt.tecnico.hds.mad.lib.security.SecurityUtils;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

/*
 * Represents a record proof, signed by some client
 */
public class Proof implements ContractObject {
    private String signerId;
    private String signature;

    public Proof(String signerId, String signature) {
        this.signerId = signerId;
        this.signature = signature;
    }

    public String getSignerId() { return this.signerId; }
    public String getSignature() { return this.signature; }

    public boolean isValid(Record record, PublicKey key) throws GeneralSecurityException {
        return SecurityUtils.checkSignature(record.toString(), this.signature, key);
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("signer_id", this.signerId);
        res.addProperty("signature", this.signature);
        return res;
    }

    public static Proof fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String id = json.remove("signer_id").getAsString();
            String sig = json.remove("signature").getAsString();

            // if remaining elements, json was not a proof
            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            return new Proof(id, sig);
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
        Proof proof = (Proof) o;
        return this.getSignerId().equals(proof.getSignerId()) &&
            this.getSignature().equals(proof.getSignature());
    }
}
