package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProofOfWorkProviderResponseTest {

    @Test
    @DisplayName("Correct de/serialization of Ok")
    public void correctDeSerializationOk() throws InvalidJsonException {
        ProofOfWorkProviderResponse request1 = ProofOfWorkProviderResponse.Ok();
        JsonObject json = request1.toJson();
        ProofOfWorkProviderResponse request2 = ProofOfWorkProviderResponse.fromJson(json);

        Assertions.assertEquals(request1, request2);
    }

    @Test
    @DisplayName("Correct de/serialization of Nok")
    public void correctDeSerializationNok() throws InvalidJsonException {
        ProofOfWorkProviderResponse request1 = ProofOfWorkProviderResponse.Nok("reason");
        JsonObject json = request1.toJson();
        ProofOfWorkProviderResponse request2 = ProofOfWorkProviderResponse.fromJson(json);

        Assertions.assertEquals(request1, request2);
    }

    @Test
    @DisplayName("Different reasons different serializations")
    public void differentReasonsJson() throws InvalidJsonException {
        ProofOfWorkProviderResponse request1 = ProofOfWorkProviderResponse.Nok("reason1");
        ProofOfWorkProviderResponse request2 = ProofOfWorkProviderResponse.Nok("reason2");
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();
        Assertions.assertNotEquals(ProofOfWorkProviderResponse.fromJson(json1), request2);
        Assertions.assertNotEquals(ProofOfWorkProviderResponse.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        ProofOfWorkProviderResponse requestOk = ProofOfWorkProviderResponse.Ok();
        ProofOfWorkProviderResponse requestNok = ProofOfWorkProviderResponse.Nok("reason");
        JsonObject jsonOk = requestOk.toJson();
        JsonObject jsonNok = requestNok.toJson();

        jsonOk.addProperty("extra", "I shouldn't be here");
        jsonNok.addProperty("extra", "I shouldn't be here");

        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkProviderResponse.fromJson(jsonOk);
        });
        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkProviderResponse.fromJson(jsonNok);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        ProofOfWorkProviderResponse requestOk = ProofOfWorkProviderResponse.Ok();
        ProofOfWorkProviderResponse requestNok = ProofOfWorkProviderResponse.Nok("reason");
        JsonObject jsonOk = requestOk.toJson();
        JsonObject jsonNok = requestNok.toJson();

        jsonOk.remove("content");
        jsonNok.remove("content");

        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkProviderResponse.fromJson(jsonOk);
        });
        assertThrows(InvalidJsonException.class, () -> {
            ProofOfWorkProviderResponse.fromJson(jsonNok);
        });
    }
}
