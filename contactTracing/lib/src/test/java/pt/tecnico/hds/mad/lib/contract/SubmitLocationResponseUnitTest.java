package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;

import static org.junit.jupiter.api.Assertions.*;

public class SubmitLocationResponseUnitTest {
    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        SubmitLocationResponse m1 = new SubmitLocationResponse(ResponseStatus.OK);
        JsonObject json = m1.toJson();
        SubmitLocationResponse m2 = SubmitLocationResponse.fromJson(json);
        assertEquals(m1, m2);
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson() throws InvalidJsonException {
        SubmitLocationResponse m1 = new SubmitLocationResponse(ResponseStatus.OK);
        SubmitLocationResponse m2 = new SubmitLocationResponse(ResponseStatus.INSUFFICIENT_EVIDENCE);
        JsonObject json1 = m1.toJson();
        JsonObject json2 = m2.toJson();
        assertNotEquals(SubmitLocationResponse.fromJson(json1), m2);
        assertNotEquals(SubmitLocationResponse.fromJson(json2), m1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() throws GeneralSecurityException {
        SubmitLocationResponse m1 = new SubmitLocationResponse(ResponseStatus.OK);
        JsonObject json = m1.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            SubmitLocationResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        SubmitLocationResponse m1 = new SubmitLocationResponse(ResponseStatus.OK);
        JsonObject json = m1.toJson();
        json.remove("status");
        assertThrows(InvalidJsonException.class, () -> {
            SubmitLocationResponse.fromJson(json);
        });
    }
}

