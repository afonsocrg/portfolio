package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.contract.MessageType;
import pt.tecnico.hds.mad.lib.contract.Proof;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidLocationProofResponseException;

public class LocationProofResponse extends ContractMessage {
    private static final MessageType type = MessageType.LOCATION_PROOF_REQUEST;

    private boolean accepted;
    private Proof proof;

    public LocationProofResponse(boolean accepted, Proof proof)
        throws InvalidLocationProofResponseException
    {
        if(accepted && proof == null || !accepted && proof != null) {
            throw new InvalidLocationProofResponseException();
        }
        this.accepted = accepted;
        this.proof = proof;
    }

    public boolean isAccepted() { return this.accepted; }
    public Proof getProof() { return this.proof; }

    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());
        res.addProperty("accepted", this.accepted);

        if(this.accepted && this.proof != null) {
            res.add("content", this.proof.toJson());
        }
        return res;
    }

    public static LocationProofResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            boolean accepted = json.remove("accepted").getAsBoolean();
            Proof proof = null;
            if(accepted) {
                proof = Proof.fromJson(json.remove("content").getAsJsonObject());
            }

            if(json.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new LocationProofResponse(accepted, proof);
        } catch (NullPointerException | InvalidLocationProofResponseException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
