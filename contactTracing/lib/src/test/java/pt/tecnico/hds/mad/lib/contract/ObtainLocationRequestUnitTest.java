package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObtainLocationRequestUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        UserLocationQuery query = new UserLocationQuery("client1", 1);
        ObtainLocationRequest request1 = new ObtainLocationRequest(query);
        JsonObject json1 = request1.toJson();
        ObtainLocationRequest request2 = ObtainLocationRequest.fromJson(json1);

        Assertions.assertEquals(request1, request2);
        Assertions.assertEquals(request1.getUserLocationQuery(), request2.getUserLocationQuery());
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson() throws InvalidJsonException {
        UserLocationQuery query1 = new UserLocationQuery("client1", 1);
        UserLocationQuery query2 = new UserLocationQuery("client1", 2);
        ObtainLocationRequest request1 = new ObtainLocationRequest(query1);
        ObtainLocationRequest request2 = new ObtainLocationRequest(query2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(ObtainLocationRequest.fromJson(json1), request2);
        Assertions.assertNotEquals(ObtainLocationRequest.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        ObtainLocationRequest request = new ObtainLocationRequest(new UserLocationQuery("client1", 1));
        JsonObject json = request.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        ObtainLocationRequest request = new ObtainLocationRequest(new UserLocationQuery("client1", 1));
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofRequest.fromJson(json);
        });
    }
}
