package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

public class LocationProofRequestUnitTest {
    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        Record record = new Record("client1", 2, 3, 4);
        LocationProofRequest m1 = new LocationProofRequest(record);
        JsonObject json = m1.toJson();
        LocationProofRequest m2 = LocationProofRequest.fromJson(json);
        assertEquals(m1, m2);
        assertEquals(record, m2.getRecord());
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson() throws InvalidJsonException {
        Record r1 = new Record("client1", 2, 3, 4);
        Record r2 = new Record("client4", 3, 2, 1);
        LocationProofRequest m1 = new LocationProofRequest(r1);
        LocationProofRequest m2 = new LocationProofRequest(r2);
        JsonObject json1 = m1.toJson();
        JsonObject json2 = m2.toJson();
        assertNotEquals(LocationProofRequest.fromJson(json1), m2);
        assertNotEquals(LocationProofRequest.fromJson(json2), m1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        LocationProofRequest m1 = new LocationProofRequest(new Record("client1", 2, 3, 4));
        JsonObject json = m1.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        LocationProofRequest m1 = new LocationProofRequest(new Record("client1", 2, 3, 4));
        JsonObject json = m1.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofRequest.fromJson(json);
        });
    }
}

