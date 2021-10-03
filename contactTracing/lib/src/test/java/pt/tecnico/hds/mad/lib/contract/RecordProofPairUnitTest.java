package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;
import pt.tecnico.hds.mad.lib.security.KeyPool;
import pt.tecnico.hds.mad.lib.security.SecurityUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RecordProofPairUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException, GeneralSecurityException {
        Record record = new Record("client1", 2, 3, 4);
        Proof proof = new Proof("client1", "INVALID BUT WE DO NOT CARE HERE");

        RecordProofPair pair = new RecordProofPair(record, proof);

        JsonObject json = pair.toJson();
        RecordProofPair pair2 = RecordProofPair.fromJson(json);

        assertEquals(pair, pair2);
    }

    @Test
    @DisplayName("Different pairs different serializations")
    public void invalidJson() throws InvalidJsonException {
        Record r1 = new Record("client1", 2, 3, 4);
        Proof p1 = new Proof("client1", "INVALID BUT WE DO NOT CARE");
        Record r2 = new Record("client4", 3, 2, 1);
        Proof p2 = new Proof("client4", "ANOTHER INVALID BUT IT'S OK");

        RecordProofPair pair1 = new RecordProofPair(r1, p1);
        RecordProofPair pair2 = new RecordProofPair(r2, p2);

        JsonObject json1 = pair1.toJson();
        JsonObject json2 = pair2.toJson();
        assertNotEquals(RecordProofPair.fromJson(json1), pair2);
        assertNotEquals(RecordProofPair.fromJson(json2), pair1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        Record r = new Record("client1", 2, 3, 4);
        Proof p = new Proof("client1", "INVALID BUT OK");
        RecordProofPair pair = new RecordProofPair(r, p);
        JsonObject json = pair.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            RecordProofPair.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        Record r = new Record("client1", 2, 3, 4);
        Proof p = new Proof("client1", "INVALID BUT OK");
        RecordProofPair pair = new RecordProofPair(r, p);
        JsonObject json = pair.toJson();
        JsonObject tmp = json.deepCopy();

        tmp.remove("record");
        JsonObject finalTmp = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            RecordProofPair.fromJson(finalTmp);
        });

        tmp = json.deepCopy();
        tmp.remove("proof");
        JsonObject finalTmp1 = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            RecordProofPair.fromJson(finalTmp1);
        });
    }
}
