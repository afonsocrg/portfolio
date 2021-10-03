package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsersAtLocationRequestUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        UsersAtLocationQuery query = new UsersAtLocationQuery(1, 1, 1);
        UsersAtLocationRequest request1 = new UsersAtLocationRequest(query);
        JsonObject json1 = request1.toJson();
        UsersAtLocationRequest request2 = UsersAtLocationRequest.fromJson(json1);

        Assertions.assertEquals(request1, request2);
        Assertions.assertEquals(request1.getUsersAtLocationQuery(), request2.getUsersAtLocationQuery());
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson() throws InvalidJsonException {
        UsersAtLocationQuery query1 = new UsersAtLocationQuery(1, 1, 1);
        UsersAtLocationQuery query2 = new UsersAtLocationQuery(2, 2, 2);
        UsersAtLocationRequest request1 = new UsersAtLocationRequest(query1);
        UsersAtLocationRequest request2 = new UsersAtLocationRequest(query2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(UsersAtLocationRequest.fromJson(json1), request2);
        Assertions.assertNotEquals(UsersAtLocationRequest.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        UsersAtLocationRequest request = new UsersAtLocationRequest(new UsersAtLocationQuery(1, 1, 1));
        JsonObject json = request.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            UsersAtLocationRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        UsersAtLocationRequest request = new UsersAtLocationRequest(new UsersAtLocationQuery(1, 1, 1));
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            UsersAtLocationRequest.fromJson(json);
        });
    }
}
