package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProofOfWorkRequesterResponseTest {

    @Test
    @DisplayName("Correct de/serialization of Ok")
    public void correctDeSerializationOk() throws InvalidJsonException {
        ProofOfWorkRequesterResponse request1 = new ProofOfWorkRequesterResponse("response");
        JsonObject json = request1.toJson();
        ProofOfWorkRequesterResponse request2 = ProofOfWorkRequesterResponse.fromJson(json);

        Assertions.assertEquals(request1, request2);
    }

    @Test
    @DisplayName("Different responses different serializations")
    public void differentResponsesJson() throws InvalidJsonException {
        ProofOfWorkRequesterResponse request1 = new ProofOfWorkRequesterResponse("response1");
        ProofOfWorkRequesterResponse request2 = new ProofOfWorkRequesterResponse("response2");
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();
        Assertions.assertNotEquals(ProofOfWorkRequesterResponse.fromJson(json1), request2);
        Assertions.assertNotEquals(ProofOfWorkRequesterResponse.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        ProofOfWorkRequesterResponse requestOk = new ProofOfWorkRequesterResponse("response");
        JsonObject json = requestOk.toJson();
        json.addProperty("extra", "I shouldn't be here");

        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkRequesterResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        ProofOfWorkRequesterResponse requestOk = new ProofOfWorkRequesterResponse("response");
        JsonObject json = requestOk.toJson();
        json.remove("content");

        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkRequesterResponse.fromJson(json);
        });
    }
}
