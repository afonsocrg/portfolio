package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class ProofOfWorkProviderResponse extends ContractMessage {
    private static final MessageType type = MessageType.PROOF_OF_WORK_PROVIDER_RESPONSE;

    private boolean ok;
    private String reason;

    private ProofOfWorkProviderResponse() {
        this.ok = true;
    }

    private ProofOfWorkProviderResponse(String reason) {
        this.ok = false;
        this.reason = reason;
    }

    public static ProofOfWorkProviderResponse Ok() {
        return new ProofOfWorkProviderResponse();
    }

    public static ProofOfWorkProviderResponse Nok(String reason) {
        return new ProofOfWorkProviderResponse(reason);
    }

    public boolean isOk() {
        return this.ok;
    }

    public String getReason() {
        return this.reason;
    }

    @Override
    public JsonObject toJson() {
        JsonObject res = new JsonObject();
        res.addProperty("type", this.type.getLabel());

        JsonObject responseContent = new JsonObject();
        responseContent.addProperty("ok", this.ok);
        if (!this.ok) {
            responseContent.addProperty("reason", this.reason);
        }
        res.add("content", responseContent);
        return res;
    }

    public static ProofOfWorkProviderResponse fromJson(JsonObject _json) throws InvalidJsonException {
        try {
            JsonObject json = _json.deepCopy();
            String _type = json.remove("type").getAsString();
            if(!_type.equals(type.getLabel())) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }

            JsonObject content = json.remove("content").getAsJsonObject();
            boolean ok = content.remove("ok").getAsBoolean();
            if (ok) {
                if (json.size() > 0 || content.size() > 0) {
                    throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
                }
                return new ProofOfWorkProviderResponse();
            }
            String reason = content.remove("reason").getAsString();

            if (json.size() > 0 || content.size() > 0) {
                throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
            }
            return new ProofOfWorkProviderResponse(reason);
        } catch (NullPointerException e) {
            throw new InvalidJsonException(InvalidJsonException.INVALID_FORMAT);
        }
    }
}
