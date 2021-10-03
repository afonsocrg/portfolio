package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProofUnitTest {

    private static final String userId = "client1";
    private static final String STORE_PASS = "globalpass";
    private static final String CLIENT_KEY = userId;
    private static final String CLIENT_PASS = userId + "pass";
    private static final String key_store_path = "test.keystore.jks";

    private static KeyPool userKeyPool;

    @BeforeAll
    public static void oneTimeSetup() throws IOException, InterruptedException, KeyPoolException {
        Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                userId, key_store_path)).waitFor();
        userKeyPool = new KeyPool(key_store_path, STORE_PASS, CLIENT_KEY, CLIENT_PASS);
    }

    @Test
    @DisplayName("Test valid proof")
    public void validProof() throws IOException, GeneralSecurityException {
        Record record = new Record("client1", 2, 3, 4);

        PrivateKey kr = userKeyPool.getPrivateKey();
        String b64sig = SecurityUtils.sign(record.toString(), kr);
        Proof proof = new Proof(userId, b64sig);

        PublicKey ku = userKeyPool.getPublicKey();
        assertTrue(proof.isValid(record, ku));
    }

    @Test
    @DisplayName("Test invalid proof")
    public void invalidProof() throws IOException, GeneralSecurityException {
        Record record = new Record("client1", 2, 3, 4);
        Record fakeRecord = new Record("client2", 2, 3, 4);

        PrivateKey kr = userKeyPool.getPrivateKey();
        String b64sig = SecurityUtils.sign(record.toString(), kr);
        Proof proof = new Proof(userId, b64sig);

        PublicKey ku = userKeyPool.getPublicKey();
        assertFalse(proof.isValid(fakeRecord, ku));
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        Proof proof1 = new Proof("client1", "INVALID SIGNATURE BUT IT DOESNT MATTER CUZ WE'RE NOT TESTING IT");

        JsonObject json = proof1.toJson();
        Proof proof2 = Proof.fromJson(json);

        assertEquals(proof1, proof2);
    }

    @Test
    @DisplayName("Different proofs different serializations")
    public void invalidJson() throws InvalidJsonException {
        Proof p1 = new Proof("client1", "Proof 1");
        Proof p2 = new Proof("client4", "Proof 2");
        JsonObject json1 = p1.toJson();
        JsonObject json2 = p2.toJson();
        assertNotEquals(Proof.fromJson(json1), p2);
        assertNotEquals(Proof.fromJson(json2), p1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        Proof proof = new Proof("client1", "Proof 1");
        JsonObject json = proof.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            Proof.fromJson(json);
        });
    }
    
    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        Proof p = new Proof("client1", "Proof 1");
        JsonObject json = p.toJson();
        JsonObject tmp = json.deepCopy();
        tmp.remove("signer_id");
        JsonObject finalTmp = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Proof.fromJson(finalTmp);
        });

        tmp = json.deepCopy();
        tmp.remove("signature");
        JsonObject finalTmp1 = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Proof.fromJson(finalTmp1);
        });
    }

    @AfterAll
    public static void deleteKeyStore() {
        (new File(key_store_path)).delete();
    }
}

