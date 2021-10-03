package pt.tecnico.hds.mad.client;

import com.google.gson.JsonObject;
import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;

import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class User implements Broadcaster {
    private static final String HELP = "^help$";
    private static final String EXIT = "^exit$";
    private static final String LOCATION = "^locationReport (.*)$";
    private final Pattern locationPattern = Pattern.compile(LOCATION);

    private static final String REQUEST_MY_PROOFS = "^myProofs (([0-9]+)(,[0-9]+)*)$";
    private final Pattern myProofsPattern = Pattern.compile(REQUEST_MY_PROOFS);

    private String serverAddress = "127.0.0.1";

    private String id;
    private Grid grid;
    private KeyPool keyPool;
    private PublicKey myKey;

    private Thread userListeningServiceThread;
    private UserListeningService listeningService;
    private int locationProofRequesterPort;

    private Thread locationProofManagerThread;
    private LocationProofManager proofService;
    private int userListeningPort;

    private final Object readOperationLock = new Object();


    public String getServerAddress() { return this.serverAddress; }

    public String getId() { return this.id; }
    public Grid getGrid() { return this.grid; }
    public KeyPool getKeyPool() { return this.keyPool; }

    public LocationProofManager getProofService() { return proofService; }
    public UserListeningService getListeningService() { return listeningService; }

    public User(String id, Grid grid, KeyPool keyPool) throws IOException {
        this.locationProofRequesterPort = Utils.getLocationRequesterPortFromId(id);
        this.userListeningPort = Utils.getListenerServicePortFromId(id);
        this.id = id;
        this.grid = grid;
        this.keyPool = keyPool;
        this.myKey = this.keyPool.getPublicKey();
        this.listeningService = new UserListeningService(this, this.userListeningPort);
        this.proofService = new LocationProofManager(this, this.locationProofRequesterPort);
    }

    public void start() {
        // 2 threads
        // 1 for user to user communication
        // 1 for requesting proofs and sending them to server
        // In order to be able to correctly shutdown, it is better to create Runnable classes
        // Master thread takes care of input
         userListeningServiceThread = new Thread(listeningService);
         userListeningServiceThread.start();

         locationProofManagerThread = new Thread(proofService);
         locationProofManagerThread.start();

         // interactive mode, to use user input
         interactive();

         userListeningServiceThread.interrupt();
         locationProofManagerThread.interrupt();

    }


    public void deliverMessage(JsonObject message) {
        // do nothing: clients do not want to receive
        // broadcast messages. they just send them
    }

    public void interactive() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("User online, write 'exit' to quit and 'help' for a list of commands");

        try {
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine().trim();

                if (Pattern.matches(EXIT, command)) break;
                else if (Pattern.matches(HELP, command)) showHelp();
                else if (Pattern.matches(LOCATION, command)) {
                    String epoch = getGroupFromPattern(command, locationPattern, 1);
                    requestLocationReport(Integer.parseInt(epoch));
                }
                else if (Pattern.matches(REQUEST_MY_PROOFS, command)) {
                    String epochs = getGroupFromPattern(command, myProofsPattern, 1);
                    String[] epochsList = epochs.split(",");
                    List<Integer> queryEpochs = Arrays.stream(epochsList).map(Integer::valueOf).collect(Collectors.toList());
                    requestMyProofs(queryEpochs);
                }
                else {
                    this.handleDefault(command);
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("End of input");
        }
    }

    private void requestLocationReport(int epoch) {

        UserLocationQuery query = new UserLocationQuery(this.id, epoch);
        ObtainLocationRequest request = new ObtainLocationRequest(query);

        JsonObject responseJson = readOperationsToServers(request, Arrays.asList(epoch));
        try {
            MessageType type = ContractMessage.getMessageType(responseJson);

            switch (type) {
                case OBTAIN_LOCATION_RESPONSE:
                    ObtainLocationResponse response = ObtainLocationResponse.fromJson(responseJson);
                    switch (response.getStatus()) {
                        case INVALID_ID_REQUEST:
                            System.out.println("You cannot request the location for that ID");
                            break;
                        case NO_INFORMATION_FOR_QUERY:
                            System.out.println("There is no information stored for this query");
                            break;
                        case OK:
                            // If majority said ok, write back the value before printing
                            trySendLocationReport(response.getUserLocationReport().getReport());
                            UserLocationReport report = response.getUserLocationReport();
                            System.out.println("I was at location " + report.toString() + " at epoch " + epoch);
                            break;
                        default:
                            System.out.println("Bad Response status!");
                    }
                    break;
                case ERROR_MESSAGE:
                    ErrorMessage message = ErrorMessage.fromJson(responseJson);
                    System.out.println(message.getError());
                    break;
                default:
                    // Ignore
            }

        } catch (InvalidJsonException e) {
            System.out.println(e.getMessage());
        }
    }

    private void requestMyProofs(List<Integer> queryEpochs) {
        MyProofsQuery query = new MyProofsQuery(this.id, queryEpochs);
        MyProofsRequest request = new MyProofsRequest(query);

        JsonObject responseJson = readOperationsToServers(request, queryEpochs);
        try {
            MessageType type = ContractMessage.getMessageType(responseJson);

            switch (type) {
                case MY_PROOFS_RESPONSE:
                    MyProofsResponse response = MyProofsResponse.fromJson(responseJson);
                    switch (response.getStatus()) {
                        case INVALID_ID_REQUEST:
                            System.out.println("You cannot request proofs for that ID");
                            break;
                        case NO_INFORMATION_FOR_QUERY:
                            System.out.println("There is no information stored for this query");
                            break;
                        case OK:
                            MyProofsReport report = response.getMyProofsReport();
                            printProofs(report);
                            break;
                        default:
                            System.out.println("Bad Response status!");
                    }
                    break;
                case ERROR_MESSAGE:
                    ErrorMessage message = ErrorMessage.fromJson(responseJson);
                    System.out.println(message.getError());
                    break;
                default:
                    // Ignore
            }

        } catch (InvalidJsonException e) {
            System.out.println(e.getMessage());
        }

    }

    private void printProofs(MyProofsReport report) {
        for (RecordProofPair pair: report.getProofs()) {
            System.out.println("I signed " + pair.getRecord().getUserId() + "'s record at epoch " + pair.getRecord().getEpoch() + " with signature " + pair.getProof().getSignature());
        }
    }

    public void trySendLocationReport(Report report) {

        SubmitLocationRequest request = new SubmitLocationRequest(report);
        DoubleEchoBroadcast deb = new DoubleEchoBroadcast(this.getId(), this, this.keyPool);

        // when this call returns, the write will be done in all correct servers
        deb.broadcast(request.toJson());
        System.out.println("Done writing on servers!");
    }

    private JsonObject readOperationsToServers(ContractMessage request, List<Integer> queryEpochs) {
        ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses = new ConcurrentHashMap<>();
        List<Thread> ongoingConnectionsToServers = new LinkedList<>();

        for (int i = 1; i <= HelperConstants.NUM_SERVERS; i++) {
            String serverId = "server" + i;
            int targetPort = Utils.getServerPortFromId(serverId);
            System.out.println("Sending to Server " + i + " at port " + targetPort);
            Thread t = new Thread(() -> sendReadRequestToServer(serverId, targetPort, request, receivedResponses, queryEpochs));
            t.start();
            ongoingConnectionsToServers.add(t);
        }

        // Keep waiting for answers until there is one that has a count above the byzantine quorum
        JsonObject majority;
        synchronized (this.readOperationLock) {
            while((majority = enoughResponses(receivedResponses)) == null) {
                try {
                    this.readOperationLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    for (Thread t: ongoingConnectionsToServers) t.interrupt();
                }
            }
        }

        return majority;
    }

    private JsonObject enoughResponses(ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses) {
        return receivedResponses.keySet().stream()
                .filter(x -> receivedResponses.get(x).get() > Utils.getByzantineQuorum())
                .findFirst()
                .orElse(null);
    }

    private void sendReadRequestToServer(
            String serverId,
            int targetPort,
            ContractMessage request,
            ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses,
            List<Integer> queryEpochs) {
        PublicKey serverPublicKey;
        try {
            serverPublicKey = this.keyPool.getPublicKey(serverId);
        } catch (KeyPoolException e) {
            System.out.println("Error getting server's public key");
            return;
        }
        while(true) {
            try(SecureSocket serverSocket =  new SecureSocket(
                    new Socket(serverAddress, targetPort),
                    this.keyPool.getPublicKey(),
                    this.keyPool.getPrivateKey(),
                    serverId,
                    serverPublicKey
            )) {
                serverSocket.identify(this.id);

                serverSocket.send(request.toJson());
                JsonObject responseJson =  serverSocket.receive();

                synchronized (this.readOperationLock) {
                    try {
                        MessageType type = ContractMessage.getMessageType(responseJson);

                        switch (type) {
                            case OBTAIN_LOCATION_RESPONSE:
                                handleLocationResponse(responseJson, receivedResponses, queryEpochs.get(0));
                                this.readOperationLock.notifyAll();
                                return;
                            case MY_PROOFS_RESPONSE:
                                handleProofsResponse(responseJson, receivedResponses, queryEpochs);
                                this.readOperationLock.notifyAll();
                                return;
                            case ERROR_MESSAGE:
                                ErrorMessage message = ErrorMessage.fromJson(responseJson);
                                if (receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                                else receivedResponses.put(responseJson, new AtomicInteger(1));
                                this.readOperationLock.notifyAll();
                                break;
                            default:
                                // Ignore
                        }

                    } catch (InvalidJsonException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IdentityException e) {
                System.err.println("Error identifying client to server. " + e.getMessage());
            } catch (GeneralSecurityException e) {
                System.err.println("Security Error: " + e.getMessage());
            } catch (IOException e) {
                // Keep trying
            }
        }
    }

    private void handleLocationResponse(
            JsonObject responseJson,
            ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses,
            int queryEpoch) throws InvalidJsonException {
        ObtainLocationResponse response = ObtainLocationResponse.fromJson(responseJson);
        switch (response.getStatus()) {
            case INVALID_ID_REQUEST:
            case NO_INFORMATION_FOR_QUERY:
                if(receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                else receivedResponses.put(responseJson, new AtomicInteger(1));
                break;
            case OK:
                UserLocationReport report = response.getUserLocationReport();
                if (validateMessage(report, queryEpoch)) {
                    if (receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                    else receivedResponses.put(responseJson, new AtomicInteger(1));

                }
                break;
            default:
                System.out.println("Bad Response status!");
        }
    }

    private void handleProofsResponse(
            JsonObject responseJson,
            ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses,
            List<Integer> queryEpochs) throws InvalidJsonException{
        MyProofsResponse response = MyProofsResponse.fromJson(responseJson);
        switch (response.getStatus()) {
            case INVALID_ID_REQUEST:
            case NO_INFORMATION_FOR_QUERY:
                if (receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                else receivedResponses.put(responseJson, new AtomicInteger(1));
                break;
            case OK:
                MyProofsReport report = response.getMyProofsReport();
                if(validateMessage(report, queryEpochs)) {
                    if (receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                    else receivedResponses.put(responseJson, new AtomicInteger(1));
                }
                break;
            default:
                System.out.println("Bad Response status!");
        }
    }

    private boolean validateMessage(UserLocationReport report, int queryEpoch) {
        // Construct Record from x, y and my request
        System.out.println("report: " + report.toJson());
        Record sentRecord = new Record(
                this.id,
                queryEpoch,
                report.getReport().getRecord().getX(),
                report.getReport().getRecord().getY()
        );
        try {
            Proof myProof = report.getReport().getProofs().stream().filter(x -> x.getSignerId().equals(this.id)).findFirst().orElseThrow(() -> new InvalidJsonException("No Proof"));

            return SecurityUtils.checkSignature(sentRecord.toString(), myProof.getSignature(), this.myKey);
        } catch (GeneralSecurityException | InvalidJsonException e) {
            return false;
        }
    }

    private boolean validateMessage(MyProofsReport report, List<Integer> queryEpochs) {

        return report.getProofs().stream().allMatch(pair -> {
            try {
                return SecurityUtils.checkSignature(pair.getRecord().toString(), pair.getProof().getSignature(), this.myKey) && queryEpochs.contains(pair.getRecord().getEpoch());
            } catch (GeneralSecurityException e) {
                return false;
            }
        });
    }

    private void handleDefault(String cmd) {
        System.out.println("unkown command: " + cmd);
    }

    private void showHelp() {
        System.out.print("User commands:\n" +
                "help                       | this screen\n" +
                "exit                       | leave application\n" +
                "locationReport [epoch]     | Show location report for given epoch\n" +
                "myProofs [list of epochs]  | Show proofs given at requested epochs (NOTE: List should be comma separated, without spaces, eg 1,2,3)\n" + "\n");
    }

    private String getGroupFromPattern(String command, Pattern pattern, int index) {
        Matcher m = pattern.matcher(command);
        m.find();
        return m.group(index);
    }
}
