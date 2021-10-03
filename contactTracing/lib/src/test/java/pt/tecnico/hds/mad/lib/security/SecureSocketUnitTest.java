package pt.tecnico.hds.mad.lib.security;

import com.google.gson.*;
import java.io.IOException;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

public class SecureSocketUnitTest {


    private static final String STORE_PASS = "globalpass";
    private static final String key_store_path = "test.keystore.jks";

    private static final String client_id = "client1";
    private static final String CLIENT_KEY_ID = client_id;
    private static final String CLIENT_KEY_PASS= client_id + "pass";

    private static final String SERVER_KEY_ID = "server1";
    private static final String SERVER_KEY_PASS = "server1pass";

    private static KeyPool serverKeyPool;
    private static KeyPool clientKeyPool;

    private static SecureSocket serverSocket;
    private static SecureSocket clientSocket;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException, KeyPoolException {
        Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                client_id, key_store_path)).waitFor();
        Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                "server1", key_store_path)).waitFor();
        clientKeyPool = new KeyPool(key_store_path, STORE_PASS, CLIENT_KEY_ID, CLIENT_KEY_PASS);
        serverKeyPool = new KeyPool(key_store_path, STORE_PASS, SERVER_KEY_ID, SERVER_KEY_PASS);

        PrivateKey ckr = clientKeyPool.getPrivateKey();
        PublicKey cku = clientKeyPool.getPublicKey();
        PrivateKey skr = serverKeyPool.getPrivateKey();
        PublicKey sku = serverKeyPool.getPublicKey();

        clientSocket = new SecureSocket(null, cku, ckr, "0", sku, true);
        serverSocket = new SecureSocket(null, sku, skr, "client1", cku, true);
    }

    private JsonObject createRequest() {
        JsonObject request = new JsonObject();

        JsonObject nested = new JsonObject();
        nested.addProperty("1", "1");
        nested.addProperty("2", "2");

        request.addProperty("label", "test");
        request.add("nested", nested);
        return request;
    }

    @Test
    @DisplayName("Checks if receiving socket receives sent message")
    public void correctSending() throws GeneralSecurityException {
        JsonObject request = createRequest();

        JsonObject envelope = clientSocket.wrapContent(request);
        JsonObject returned = serverSocket.unwrapContent(envelope.toString());

        assertEquals(request, returned);
    }

    @Test
    @DisplayName("Try to unwrap message with missing fields")
    public void missingFields() throws GeneralSecurityException {
        JsonObject request = createRequest();
        JsonObject envelope = clientSocket.wrapContent(request);

        JsonObject copy = envelope.deepCopy();
        copy.remove("signature");
        assertThrows(GeneralSecurityException.class, () -> {
            serverSocket.unwrapContent(copy.toString());
        });

        // need to create a new object bc compiler errors saying
        // "local variables referenced from a lambda expression 
        // must be final or effectively final"
        JsonObject copy2 = envelope.deepCopy();
        copy2.remove("request");
        assertThrows(GeneralSecurityException.class, () -> {
            serverSocket.unwrapContent(copy2.toString());
        });
    }

    @Test
    @DisplayName("Invalid signature")
    public void invalidSignature() throws GeneralSecurityException {
        JsonObject request = createRequest();
        JsonObject envelope = clientSocket.wrapContent(request);

        envelope.addProperty("signature", "INVALID SIGNATURE!!!");
        assertThrows(GeneralSecurityException.class, () -> {
            serverSocket.unwrapContent(envelope.toString());
        });
    }

    @Test
    @DisplayName("Invalid IV")
    public void invalidIv() throws GeneralSecurityException {
        JsonObject request = createRequest();
        JsonObject envelope = clientSocket.wrapContent(request);

        envelope.addProperty("iv", "INVALID IV!!!");
        assertThrows(GeneralSecurityException.class, () -> {
            serverSocket.unwrapContent(envelope.toString());
        });
    }

    @Test
    @DisplayName("Invalid Key")
    public void invalidKey() throws GeneralSecurityException {
        JsonObject request = createRequest();
        JsonObject envelope = clientSocket.wrapContent(request);

        envelope.addProperty("key", "INVALID KEY!!!");
        assertThrows(GeneralSecurityException.class, () -> {
            serverSocket.unwrapContent(envelope.toString());
        });
    }

    @Test
    @DisplayName("Invalid Content")
    public void invalidContent() throws GeneralSecurityException {
        JsonObject request = createRequest();
        JsonObject envelope = clientSocket.wrapContent(request);

        envelope.addProperty("content", "User was not at this location! (hue hue hue)");
        assertThrows(GeneralSecurityException.class, () -> {
            serverSocket.unwrapContent(envelope.toString());
        });
    }


    @AfterAll
    public static void deleteKeyStore() {
        (new File(key_store_path)).delete();
    }
}

