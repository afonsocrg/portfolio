package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyProofsRequestUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        MyProofsQuery query = new MyProofsQuery("client1", Arrays.asList(1, 2));
        MyProofsRequest request1 = new MyProofsRequest(query);
        JsonObject json1 = request1.toJson();
        MyProofsRequest request2 = MyProofsRequest.fromJson(json1);

        Assertions.assertEquals(request1, request2);
        Assertions.assertEquals(request1.getMyProofsQuery(), request2.getMyProofsQuery());
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson() throws InvalidJsonException {
        MyProofsQuery query1 = new MyProofsQuery("client1", Arrays.asList(1, 2));
        MyProofsQuery query2 = new MyProofsQuery("client2", Arrays.asList(3, 4));
        MyProofsRequest request1 = new MyProofsRequest(query1);
        MyProofsRequest request2 = new MyProofsRequest(query2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(MyProofsRequest.fromJson(json1), request2);
        Assertions.assertNotEquals(MyProofsRequest.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        MyProofsRequest request = new MyProofsRequest(new MyProofsQuery("client1", Arrays.asList(1, 2)));
        JsonObject json = request.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            MyProofsRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        MyProofsRequest request = new MyProofsRequest(new MyProofsQuery("client1", Arrays.asList(1, 2)));
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            MyProofsRequest.fromJson(json);
        });
    }
}
