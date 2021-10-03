package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorMessageUnitTest {
    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        ErrorMessage m1 = new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        JsonObject json = m1.toJson();
        ErrorMessage m2 = ErrorMessage.fromJson(json);
        assertEquals(m1, m2);
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson() throws InvalidJsonException {
        ErrorMessage m1 = new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        ErrorMessage m2 = new ErrorMessage(ErrorMessage.INVALID_REQUEST_FORMAT);
        JsonObject json1 = m1.toJson();
        JsonObject json2 = m2.toJson();
        assertNotEquals(ErrorMessage.fromJson(json1), m2);
        assertNotEquals(ErrorMessage.fromJson(json2), m1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        ErrorMessage m1 = new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        JsonObject json = m1.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            ErrorMessage.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        ErrorMessage m1 = new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        JsonObject json = m1.toJson();
        json.remove("error");
        assertThrows(InvalidJsonException.class, () -> {
            ErrorMessage.fromJson(json);
        });
    }
}

