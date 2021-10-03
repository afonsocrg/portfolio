package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import java.security.PublicKey;
import java.security.PrivateKey;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.*;

import static org.junit.jupiter.api.Assertions.*;

public class RecordUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        Record record1 = new Record("client1", 2, 3, 4);

        JsonObject json = record1.toJson();
        Record record2 = Record.fromJson(json);

        assertEquals(record1, record2);
    }

    @Test
    @DisplayName("Different records different serializations")
    public void invalidJson() throws InvalidJsonException {
        Record r1 = new Record("client1", 2, 3, 4);
        Record r2 = new Record("client4", 3, 2, 1);
        JsonObject json1 = r1.toJson();
        JsonObject json2 = r2.toJson();
        assertNotEquals(Record.fromJson(json1), r2);
        assertNotEquals(Record.fromJson(json2), r1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        Record r = new Record("client1", 2, 3, 4);
        JsonObject json = r.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            Record.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        Record r = new Record("client1", 2, 3, 4);
        JsonObject json = r.toJson();
        JsonObject tmp = json.deepCopy();

        tmp.remove("user_id");
        JsonObject finalTmp = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Record.fromJson(finalTmp);
        });

        tmp = json.deepCopy();
        tmp.remove("epoch");
        JsonObject finalTmp1 = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Record.fromJson(finalTmp1);
        });

        tmp = json.deepCopy();
        tmp.remove("x");
        JsonObject finalTmp2 = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Record.fromJson(finalTmp2);
        });

        tmp = json.deepCopy();
        tmp.remove("y");
        JsonObject finalTmp3 = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Record.fromJson(finalTmp3);
        });
    }
}

