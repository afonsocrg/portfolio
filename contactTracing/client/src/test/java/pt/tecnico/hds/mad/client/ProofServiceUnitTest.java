package pt.tecnico.hds.mad.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import java.security.GeneralSecurityException;
import pt.tecnico.hds.mad.client.exceptions.InvalidRecordException;
import pt.tecnico.hds.mad.client.exceptions.UserException;
import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;
import pt.tecnico.hds.mad.lib.security.*;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.security.Key;
import java.security.PrivateKey;
import java.time.LocalTime;

public class ProofServiceUnitTest extends BaseUnitTest{

    private static Grid grid_1;
    private static final String client_id = "client1";
    private static final String STORE_PASS = "globalpass";
    private static final String KEY_ID = client_id;
    private static final String KEY_PASS= client_id + "pass";
    private static final String key_store_path = "test.keystore.jks";

    private static User user1;

    @BeforeAll
    public static void setUp() {
        try {
            Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                   client_id, key_store_path)).waitFor();
            grid_1 = new Grid(testProps.getProperty("grid.config"));
            createUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createUser() throws IOException, KeyPoolException {
        KeyPool keyPool = new KeyPool(key_store_path, STORE_PASS, KEY_ID, KEY_PASS);
        user1 = new User("client1", grid_1, keyPool);
    }

    @Test
    @DisplayName("Test correct proof response")
    public void proofResponseCorrect() throws GeneralSecurityException {

        try {
            // Fake proof request of client 2
            Record record = new Record("client2", 0, 2, 2);

            Proof expected = getExpectedProof(record);
            Proof userProof =  user1.getListeningService().proveRecord(record);

            Assertions.assertEquals(expected, userProof);

        } catch (UserException | InvalidRecordException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Test correct proof response at past epoch")
    public void proofResponseCorrectPast() throws GeneralSecurityException {

        try {
            // Fake proof request of client 2
            Record record = new Record("client2", 0, 2, 2);

            Proof expected = getExpectedProof(record);
            Proof userProof = user1.getListeningService().proveRecord(record);

            Assertions.assertEquals(expected, userProof);

        } catch (UserException | InvalidRecordException e) {
            e.printStackTrace();
        }
    }

    private Proof getExpectedProof(Record record) throws GeneralSecurityException {
        PrivateKey key = user1.getKeyPool().getPrivateKey();
        String b64sig = SecurityUtils.sign(record.toString(), key);

        return new Proof("client1", b64sig);
    }

    @Test
    @DisplayName("Test correct proof response at future epoch")
    public void proofResponseCorrectFuture() throws GeneralSecurityException {

        try {
            // Fake proof request of client 2
            Record record = new Record("client2", 1, 2, 2);

            Proof expected = getExpectedProof(record);
            Proof userProof =  user1.getListeningService().proveRecord(record);

            Assertions.assertEquals(expected, userProof);

        } catch (UserException | InvalidRecordException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Test wrong location proof response")
    public void proofResponseBadLocation() {
        // Fake bad proof request of client 2
        Record record = new Record("client2", 0, 3, 3);
        InvalidRecordException e = Assertions.assertThrows(InvalidRecordException.class, () -> {
            user1.getListeningService().proveRecord(record);
        });
        Assertions.assertEquals(InvalidRecordException.INVALID_POSITION, e.getMessage());
    }

    @Test
    @DisplayName("Test far Id proof response")
    public void proofResponseFarId() {
        // Fake bad proof request of client 3
        Record record = new Record("client3", 0, 10, 10);
        InvalidRecordException e = Assertions.assertThrows(InvalidRecordException.class, () -> {
            user1.getListeningService().proveRecord(record);
        });
        Assertions.assertEquals(InvalidRecordException.TOO_FAR, e.getMessage());
    }

    @AfterAll
    public static void deleteKeyStore() {
        (new File(key_store_path)).delete();
    }
}
