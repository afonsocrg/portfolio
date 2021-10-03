package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProofOfWorkRequestTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        ProofOfWorkRequest request1 = new ProofOfWorkRequest("id", 1);
        JsonObject json = request1.toJson();
        ProofOfWorkRequest request2 = ProofOfWorkRequest.fromJson(json);

        Assertions.assertEquals(request1, request2);
    }

    @Test
    @DisplayName("Different costs different serializations")
    public void differentCostsJson() throws InvalidJsonException {
        ProofOfWorkRequest request1 = new ProofOfWorkRequest("id", 1);
        ProofOfWorkRequest request2 = new ProofOfWorkRequest("id", 2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();
        Assertions.assertNotEquals(ProofOfWorkRequest.fromJson(json1), request2);
        Assertions.assertNotEquals(ProofOfWorkRequest.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Different ids different serializations")
    public void differentIdsJson() throws InvalidJsonException{
        ProofOfWorkRequest request1 = new ProofOfWorkRequest("id1", 1);
        ProofOfWorkRequest request2 = new ProofOfWorkRequest("id2", 1);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();
        Assertions.assertNotEquals(ProofOfWorkRequest.fromJson(json1), request2);
        Assertions.assertNotEquals(ProofOfWorkRequest.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        ProofOfWorkRequest request = new ProofOfWorkRequest("id", 1);
        JsonObject json = request.toJson();
        json.addProperty("extra", "I shouldn't be here");
        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        ProofOfWorkRequest request = new ProofOfWorkRequest("id", 1);
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkRequest.fromJson(json);
        });
    }
}
