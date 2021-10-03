package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;

import static org.junit.jupiter.api.Assertions.*;

public class LocationProofResponseUnitTest {

    private static final String userId = "client1";
    private static final String STORE_PASS = "globalpass";
    private static final String CLIENT_KEY = userId;
    private static final String CLIENT_PASS = userId + "pass";
    private static final String key_store_path = "test.keystore.jks";

    private static Proof proof1;
    private static Proof proof2;

    @BeforeAll
    public static void setUp()
        throws IOException, InterruptedException, KeyPoolException, GeneralSecurityException {

        Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                userId, key_store_path)).waitFor(); //TODO: Marcelo can you change the script afterwards

        KeyPool keyPool = new KeyPool(key_store_path, STORE_PASS, CLIENT_KEY, CLIENT_PASS);

        Record record1 = new Record(userId, 2, 3, 4);
        Record record2 = new Record("client4", 3, 2, 1);
        PrivateKey kr = keyPool.getPrivateKey();
        String b64sig1 = SecurityUtils.sign(record1.toString(), kr);
        String b64sig2 = SecurityUtils.sign(record2.toString(), kr);
        proof1 = new Proof(userId, b64sig1);
        proof2 = new Proof("client2", b64sig2);
    }

    @Test
    @DisplayName("Invalid rejected response with proof")
    public void rejectedWithProof() throws InvalidLocationProofResponseException {
        assertThrows(InvalidLocationProofResponseException.class, () -> {
            new LocationProofResponse(false, proof1);
        });
    }

    @Test
    @DisplayName("Invalid accepted response with no proof")
    public void acceptedNoProof() throws InvalidLocationProofResponseException {
        assertThrows(InvalidLocationProofResponseException.class, () -> {
            new LocationProofResponse(true, null);
        });
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization()
        throws InvalidJsonException,
          InvalidLocationProofResponseException
    {
        LocationProofResponse m1 = new LocationProofResponse(true, proof1);
        JsonObject json = m1.toJson();
        LocationProofResponse m2 = LocationProofResponse.fromJson(json);
        assertEquals(m1, m2);
        assertEquals(proof1, m2.getProof());
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void invalidJson()
        throws InvalidJsonException,
          InvalidLocationProofResponseException
    {
        LocationProofResponse m1 = new LocationProofResponse(true, proof1);
        LocationProofResponse m2 = new LocationProofResponse(true, proof2);
        JsonObject json1 = m1.toJson();
        JsonObject json2 = m2.toJson();
        assertNotEquals(LocationProofResponse.fromJson(json1), m2);
        assertNotEquals(LocationProofResponse.fromJson(json2), m1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() throws InvalidLocationProofResponseException {
        LocationProofResponse m1 = new LocationProofResponse(true, proof1);
        JsonObject json = m1.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing accepted field)")
    public void jsonMissingFields1() throws InvalidLocationProofResponseException {
        LocationProofResponse m1 = new LocationProofResponse(true, proof1);
        JsonObject json = m1.toJson();
        json.remove("accepted");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (accepted without proof)")
    public void jsonMissingFields2() throws InvalidLocationProofResponseException {
        LocationProofResponse m1 = new LocationProofResponse(true, proof1);
        JsonObject json = m1.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (rejected with proof)")
    public void jsonMissingFields3() throws InvalidLocationProofResponseException {
        LocationProofResponse m1 = new LocationProofResponse(true, proof1);
        JsonObject json = m1.toJson();
        json.remove("accepted");
        json.addProperty("accepted", false);
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });
    }

    @AfterAll
    public static void deleteKeyStore() {
        (new File(key_store_path)).delete();
    }
}

