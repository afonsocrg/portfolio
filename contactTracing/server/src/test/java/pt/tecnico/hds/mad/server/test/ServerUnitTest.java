package pt.tecnico.hds.mad.server.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.util.*;

import org.junit.jupiter.api.*;

import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.exceptions.InvalidObtainLocationResponseException;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;
import pt.tecnico.hds.mad.server.Database;
import pt.tecnico.hds.mad.server.exceptions.*;
import pt.tecnico.hds.mad.server.Server;


public class ServerUnitTest {
    static final String TEST_PROP_FILE = "/test.properties";
    private static final String store_pass = "globalpass";
    private static final String server_key_id = "server1";
    private static final String server_key_pass = server_key_id + "pass";
    private static final String client_key_base = "client";
    private static final String client_key_pass_base= "pass";
    private static final String key_store_path = "test.keystore.jks";
    static Properties testProps;

    static Database db;
    static Server server;
    static KeyPool serverKeyPool;
    static HashMap<Integer, KeyPool> testClientKeyPools = new HashMap<>();

    @BeforeAll
    public static void oneTimeSetup() throws IOException, InterruptedException, KeyPoolException  {
        testProps = new Properties();
        testProps.load(ServerUnitTest.class.getResourceAsStream(TEST_PROP_FILE));
        Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                server_key_id, key_store_path)).waitFor();
        generateKeyPools();
        serverKeyPool = new KeyPool(key_store_path, store_pass, server_key_id, server_key_pass);
    }

    private static void generateKeyPools() throws IOException, InterruptedException, KeyPoolException {
        for (int i = 1; i <= 4; i++ ) {
            Runtime.getRuntime().exec(String.format("../genkey.sh %s %s",
                    "client" + i, key_store_path)).waitFor();
            testClientKeyPools.put(i, new KeyPool(key_store_path, store_pass, client_key_base+i, client_key_base + i + client_key_pass_base));
        }
    }

    @BeforeEach
    public void setUp() throws DatabaseCreationException {
        String path = testProps.getProperty("db_path");
        db = new Database(path);
        String myId = "server1";
        server = new Server(myId, db, serverKeyPool);
    }


    @Test
    @DisplayName("Process valid client Report")
    void processClientReport() throws KeyPoolException, GeneralSecurityException {
        String userId = "client1";
        int epoch = 1;
        Record record = new Record(userId, epoch, 3, 4);

        List<Proof> proofs = new LinkedList<>();
        proofs.add(new Proof("client1", SecurityUtils.sign(record.toString(), testClientKeyPools.get(1).getPrivateKey())));
        proofs.add(new Proof("client2", SecurityUtils.sign(record.toString(), testClientKeyPools.get(2).getPrivateKey())));
        proofs.add(new Proof("client3", SecurityUtils.sign(record.toString(), testClientKeyPools.get(3).getPrivateKey())));
        proofs.add(new Proof("client4", SecurityUtils.sign(record.toString(), testClientKeyPools.get(4).getPrivateKey())));

        Report report = new Report(record, proofs);

        assertTrue(server.validateReport(report));
        assertEquals(4, report.getNumProofs());
    }

    @Test
    @DisplayName("Process valid client Report (with duplicate proofs)")
    void processClientReportDuplicateProofs() throws KeyPoolException, GeneralSecurityException {
        String userId = "client1";
        int epoch = 1;
        Record record = new Record(userId, epoch, 3, 4);

        List<Proof> proofs = new LinkedList<>();
        proofs.add(new Proof("client1", SecurityUtils.sign(record.toString(), testClientKeyPools.get(1).getPrivateKey())));
        proofs.add(new Proof("client2", SecurityUtils.sign(record.toString(), testClientKeyPools.get(2).getPrivateKey())));
        proofs.add(new Proof("client2", SecurityUtils.sign(record.toString(), testClientKeyPools.get(2).getPrivateKey())));
        proofs.add(new Proof("client2", SecurityUtils.sign(record.toString(), testClientKeyPools.get(2).getPrivateKey())));
        proofs.add(new Proof("client2", SecurityUtils.sign(record.toString(), testClientKeyPools.get(2).getPrivateKey())));
        proofs.add(new Proof("client3", SecurityUtils.sign(record.toString(), testClientKeyPools.get(3).getPrivateKey())));

        Report report = new Report(record, proofs);

        assertTrue(server.validateReport(report));
        assertEquals(3, report.getNumProofs());
    }

    @Test
    @DisplayName("Save Report")
    void testSaveReport() {
        // TODO: change in future: signatures must be valid
        String userId = "client1";
        int epoch = 1;
        Record record = new Record(userId, epoch, 3, 4);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);

        Report report = new Report(record, proofs);
        
        try {
            server.saveReport(report);
            Record dbRecord = db.getRecord(userId, epoch);
            assertEquals(dbRecord, record);

            List<Proof> dbproofs = db.getProofs(userId, epoch);
            assertTrue(dbproofs.contains(proof1));
            assertTrue(dbproofs.contains(proof2));
            assertTrue(dbproofs.contains(proof3));
        } catch (DatabaseException e) {
            fail();
        }
    }

    @Test
    @DisplayName("Process correct obtain location Request")
    public void validObtainLocationRequest() throws DatabaseException, InvalidObtainLocationResponseException {
        String userId = "client1";
        int epoch = 1;
        int x = 3;
        int y = 4;
        Record record = new Record(userId, epoch, x, y);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);

        Report report = new Report(record, proofs);
        db.setReport(report);

        ObtainLocationResponse correct = new ObtainLocationResponse(ResponseStatus.OK, new UserLocationReport(report));
        ObtainLocationRequest request = new ObtainLocationRequest(new UserLocationQuery(userId, epoch));

        ContractMessage response = server.handleObtainLocation(request, userId);
        Assertions.assertEquals(correct, response);
    }

    @Test
    @DisplayName("Process obtain location query for non existent query")
    public void noInformationRequest() throws InvalidObtainLocationResponseException {
        String userId = "client1";
        int epoch = 1;

        ObtainLocationResponse correct = new ObtainLocationResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null);
        ObtainLocationRequest request = new ObtainLocationRequest(new UserLocationQuery(userId, epoch));

        ContractMessage response = server.handleObtainLocation(request, userId);
        Assertions.assertEquals(correct, response);
    }

    @Test
    @DisplayName("Process obtain location query from client that is not the same as the query")
    public void incorrectClientRequest() throws InvalidObtainLocationResponseException {
        String userId = "client1";
        String requesterUserId = "client2";
        int epoch = 1;

        ObtainLocationResponse correct = new ObtainLocationResponse(ResponseStatus.INVALID_ID_REQUEST, null);
        ObtainLocationRequest request = new ObtainLocationRequest(new UserLocationQuery(userId, epoch));

        ContractMessage response = server.handleObtainLocation(request, requesterUserId);
        Assertions.assertEquals(correct, response);
    }

    @Test
    @DisplayName("Process obtain location query from ha")
    public void correctHARequest() throws InvalidObtainLocationResponseException, DatabaseException {
        String userId = "client1";
        String haId = "ha1";
        int epoch = 1;
        int x = 3;
        int y = 4;
        Record record = new Record(userId, epoch, x, y);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);


        Report report = new Report(record, proofs);
        db.setReport(report);

        ObtainLocationResponse correct = new ObtainLocationResponse(ResponseStatus.OK, new UserLocationReport(report));
        ObtainLocationRequest request = new ObtainLocationRequest(new UserLocationQuery(userId, epoch));

        ContractMessage response = server.handleObtainLocation(request, haId);
        Assertions.assertEquals(correct, response);
    }

    @Test
    @DisplayName("Process correct users at location request with 1 user")
    public void validOneUserAtLocationRequest() throws DatabaseException {
        String userId = "client1";
        int epoch = 2;
        int x = 3;
        int y = 4;

        Record record = new Record(userId, epoch, x, y);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);

        db.setReport(new Report(record, proofs));

        Map<String, Proof> correctList = new HashMap<>();
        correctList.put(userId, proof1);
        UsersAtLocationResponse correctResponse = new UsersAtLocationResponse(ResponseStatus.OK, new UsersAtLocationReport(correctList));
        UsersAtLocationRequest request = new UsersAtLocationRequest(new UsersAtLocationQuery(x, y, epoch));

        ContractMessage response = server.handleUsersAtLocation(request);
        Assertions.assertEquals(correctResponse, response);

    }


    @Test
    @DisplayName("Process correct users at location request with 2 users")
    public void validTwoUsersAtLocationRequest() throws DatabaseException {
        String userId_1 = "client1";
        String userId_2 = "client2";
        int epoch = 1;
        int x = 3;
        int y = 4;

        Record record1 = new Record(userId_1, epoch, x, y);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);

        db.setReport(new Report(record1, proofs));

        Record record2 = new Record(userId_2, epoch, x, y);

        Proof proof4 = new Proof("client1", "valid signature 1");
        Proof proof5 = new Proof("client2", "valid signature 2");
        Proof proof6 = new Proof("client3", "valid signature 3");

        List<Proof> proofs1 = new LinkedList<>();
        proofs1.add(proof4);
        proofs1.add(proof5);
        proofs1.add(proof6);

        db.setReport(new Report(record2, proofs1));

        Map<String, Proof> correctList = new HashMap<>();
        correctList.put(userId_1, proof1);
        correctList.put(userId_2, proof5);
        UsersAtLocationResponse correctResponse = new UsersAtLocationResponse(ResponseStatus.OK, new UsersAtLocationReport(correctList));
        UsersAtLocationRequest request = new UsersAtLocationRequest(new UsersAtLocationQuery(x, y, epoch));

        ContractMessage response = server.handleUsersAtLocation(request);
        Assertions.assertEquals(correctResponse, response);
    }

    @Test
    @DisplayName("Process location with no users")
    public void noUsersAtLocationRequest() {
        int epoch = 1;
        int x = 3;
        int y = 4;

        UsersAtLocationResponse correctResponse = new UsersAtLocationResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null);
        UsersAtLocationRequest request = new UsersAtLocationRequest(new UsersAtLocationQuery(x, y, epoch));

        ContractMessage response = server.handleUsersAtLocation(request);
        Assertions.assertEquals(correctResponse, response);
    }

    @Test
    @DisplayName("Process correct request my proofs")
    public void validRequestMyProofs() throws DatabaseException {
        String userId = "client1";
        int epoch = 1;
        int x = 3;
        int y = 4;

        Record record = new Record("client2", epoch, x, y);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);

        db.setReport(new Report(record, proofs));

        List<RecordProofPair> pairList = new ArrayList<>();
        RecordProofPair pair = new RecordProofPair(record, proof1);
        pairList.add(pair);

        MyProofsResponse correct = new MyProofsResponse(ResponseStatus.OK, new MyProofsReport(pairList));
        MyProofsRequest request = new MyProofsRequest(new MyProofsQuery(userId, Arrays.asList(epoch)));

        ContractMessage response = server.handleRequestMyProofs(request, userId);
        Assertions.assertEquals(correct, response);
    }


    @Test
    @DisplayName("Process request my proofs query for non existent query")
    public void noInformationMyProofsRequest() {
        String userId = "client1";
        int epoch = 1;

        MyProofsResponse correct = new MyProofsResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null);
        MyProofsRequest request = new MyProofsRequest(new MyProofsQuery(userId, Arrays.asList(epoch)));

        ContractMessage response = server.handleRequestMyProofs(request, userId);
        Assertions.assertEquals(correct, response);
    }

    @Test
    @DisplayName("Process request my proofs query from client that is not the same as the query")
    public void incorrectClientMyProofsRequest() {
        String userId = "client1";
        String requesterUserId = "client2";
        int epoch = 1;

        MyProofsResponse correct = new MyProofsResponse(ResponseStatus.INVALID_ID_REQUEST, null);
        MyProofsRequest request = new MyProofsRequest(new MyProofsQuery(userId, Arrays.asList(epoch)));

        ContractMessage response = server.handleRequestMyProofs(request, requesterUserId);
        Assertions.assertEquals(correct, response);
    }


    private Report getNewReportForUser(String id, int epoch, int x, int y) {
        Record record = new Record(id, epoch, x, y);

        Proof proof1 = new Proof("client1", "valid signature 1");
        Proof proof2 = new Proof("client2", "valid signature 2");
        Proof proof3 = new Proof("client3", "valid signature 3");

        List<Proof> proofs = new LinkedList<>();
        proofs.add(proof1);
        proofs.add(proof2);
        proofs.add(proof3);

        return new Report(record, proofs);
    }

    @AfterEach
    public void deleteDb() {
        String path = testProps.getProperty("db_path");
        TestHelper.deleteDatabase(path);
    }

    @AfterAll
    public static void deleteKeyStore() {
        (new File(key_store_path)).delete();
    }
}
