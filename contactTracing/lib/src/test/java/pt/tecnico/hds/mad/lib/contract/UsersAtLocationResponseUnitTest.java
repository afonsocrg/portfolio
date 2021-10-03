package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidObtainLocationResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsersAtLocationResponseUnitTest {

    private static UsersAtLocationReport report1;
    private static UsersAtLocationReport report2;
    private static UsersAtLocationReport report3;

    @BeforeAll
    public static void setUp() {
        Proof p1 = new Proof("client1", "INVALID BUT OK");
        Proof p2 = new Proof("client2", "INVALID BUT OK");

        Map<String, Proof> map1 = new HashMap<>();
        map1.put("client1", p1);
        report1 = new UsersAtLocationReport(map1);

        Map<String, Proof> map2 = new HashMap<>();
        map2.put("client2", p2);
        report2 = new UsersAtLocationReport(map2);

        Map<String, Proof> map3 = new HashMap<>();
        map3.put("client1", p1);
        map3.put("client2", p2);
        report3 = new UsersAtLocationReport(map3);
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException, InvalidObtainLocationResponseException {
        UsersAtLocationResponse request1 = new UsersAtLocationResponse(ResponseStatus.OK, report1);
        JsonObject json1 = request1.toJson();
        UsersAtLocationResponse request2 = UsersAtLocationResponse.fromJson(json1);

        Assertions.assertEquals(request1, request2);
        Assertions.assertEquals(request1.getUsersAtLocationReport(), request2.getUsersAtLocationReport());
    }

    @Test
    @DisplayName("Different messages different sizes different serializations")
    public void differentSizesJson() throws InvalidJsonException {
        UsersAtLocationResponse request1 = new UsersAtLocationResponse(ResponseStatus.OK, report1);
        UsersAtLocationResponse request2 = new UsersAtLocationResponse(ResponseStatus.OK, report3);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(UsersAtLocationResponse.fromJson(json1), request2);
        Assertions.assertNotEquals(UsersAtLocationResponse.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Different messages different ids different serializations")
    public void differentIdsJson() throws InvalidJsonException{
        UsersAtLocationResponse request1 = new UsersAtLocationResponse(ResponseStatus.OK, report1);
        UsersAtLocationResponse request2 = new UsersAtLocationResponse(ResponseStatus.OK, report2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(UsersAtLocationResponse.fromJson(json1), request2);
        Assertions.assertNotEquals(UsersAtLocationResponse.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() throws InvalidObtainLocationResponseException {
        UsersAtLocationResponse request = new UsersAtLocationResponse(ResponseStatus.OK, report1);
        JsonObject json = request.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            UsersAtLocationResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() throws InvalidObtainLocationResponseException {
        UsersAtLocationResponse request = new UsersAtLocationResponse(ResponseStatus.OK, report1);
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            UsersAtLocationResponse.fromJson(json);
        });
    }
}
