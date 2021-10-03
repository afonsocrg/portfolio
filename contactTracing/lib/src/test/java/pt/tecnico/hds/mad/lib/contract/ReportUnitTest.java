package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.LinkedList;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReportUnitTest {
    private static final String userId = "client1";
    private static final String STORE_PASS = "globalpass";
    private static final String CLIENT_KEY = userId;
    private static final String CLIENT_PASS = userId + "pass";
    private static final String key_store_path = "test.keystore.jks";

    private static Report report1;
    private static Report report2;

    @BeforeAll
    public static void setUp()
        throws IOException, GeneralSecurityException, InterruptedException, KeyPoolException {
        Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                userId, key_store_path)).waitFor();

        KeyPool keyPool = new KeyPool(key_store_path, STORE_PASS, CLIENT_KEY, CLIENT_PASS);

        // Create report 1
        Record record1 = new Record(userId, 2, 3, 4);
        String b64sig1 = SecurityUtils.sign(record1.toString(), keyPool.getPrivateKey());
        List<Proof> proofs1 = new LinkedList();
        proofs1.add(new Proof(userId, b64sig1));
        report1 = new Report(record1, proofs1);

        // Create report 2
        Record record2 = new Record("client4", 3, 2, 1);
        String b64sig2 = SecurityUtils.sign(record2.toString(), keyPool.getPrivateKey());
        List<Proof> proofs2 = new LinkedList();
        proofs2.add(new Proof(userId, b64sig2));
        report2 = new Report(record2, proofs2);
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        JsonObject json = this.report1.toJson();
        Report r2 = Report.fromJson(json);

        assertEquals(this.report1, r2);
    }

    @Test
    @DisplayName("Different reports different serializations")
    public void invalidJson() throws InvalidJsonException {
        JsonObject json1 = this.report1.toJson();
        JsonObject json2 = this.report2.toJson();
        assertNotEquals(Report.fromJson(json1), this.report2);
        assertNotEquals(Report.fromJson(json2), this.report1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() {
        JsonObject json = this.report1.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            Report.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() {
        JsonObject json = this.report1.toJson();
        JsonObject tmp = json.deepCopy();
        tmp.remove("record");
        JsonObject finalTmp = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Report.fromJson(finalTmp);
        });

        tmp = json.deepCopy();
        tmp.remove("proofs");
        JsonObject finalTmp1 = tmp;
        assertThrows(InvalidJsonException.class, () -> {
            Report.fromJson(finalTmp1);
        });
    }

    @AfterAll
    public static void deleteKeyStore() {
        (new File(key_store_path)).delete();
    }
}

