package pt.tecnico.hds.mad.haclient;

import com.google.gson.JsonObject;

import java.security.GeneralSecurityException;

import pt.tecnico.hds.mad.lib.HelperConstants;
import pt.tecnico.hds.mad.lib.Utils;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.IdentityException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;
import pt.tecnico.hds.mad.lib.security.*;

import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HAClient {
    private static final String HELP = "^help$";
    private static final String EXIT = "^exit$";
    private static final String LOCATION_REPORT = "^locationReport (client[0-9]+) ([0-9]+)?$";
    private static final String USERS_AT_LOCATION = "^usersAtLocation \\(([0-9]+),([0-9]+)\\) ([0-9]+)?$";
    private static final int HA_CLIENT_PORT_BASE = 6090;

    private final Object readOperationLock = new Object();

    private final Pattern locationReportPattern = Pattern.compile(LOCATION_REPORT);
    private final Pattern usersAtLocationPattern = Pattern.compile(USERS_AT_LOCATION);

    private final String server_host = "localhost";

    private String id;
    private int haPort;

    private KeyPool keyPool;

    public HAClient(String id, KeyPool keyPool) {
        this.id = id;
        this.haPort = HA_CLIENT_PORT_BASE + Utils.extractIdNumberFromString(this.id);
        this.keyPool = keyPool;
    }

    public void interactive() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("HA user online, type 'help' for a list of commands, and 'exit' to terminate");

        try {
            while(true) {
                System.out.print("> ");
                String command = scanner.nextLine().trim();

                if(Pattern.matches(EXIT, command)) break;
                else if(Pattern.matches(HELP, command)) showHelp();
                else if(Pattern.matches(LOCATION_REPORT, command)) {
                    String userId = getGroupFromPattern(command, locationReportPattern, 1);
                    String epoch = getGroupFromPattern(command, locationReportPattern, 2);
                    requestLocationReport(userId, Integer.parseInt(epoch));

                } else if(Pattern.matches(USERS_AT_LOCATION, command)) {
                    String x = getGroupFromPattern(command, usersAtLocationPattern, 1);
                    String y = getGroupFromPattern(command, usersAtLocationPattern, 2);
                    String epoch = getGroupFromPattern(command, usersAtLocationPattern, 3);
                    requestUsersAtLocation(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(epoch));

                } else handleDefault(command);
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private void requestLocationReport(String userId, int epoch) throws IOException, SecurityException {
        UserLocationQuery userLocationQuery = new UserLocationQuery(userId, epoch);
        ObtainLocationRequest request = new ObtainLocationRequest(userLocationQuery);

        JsonObject responseJson = readOperationsToServers(request, userId, epoch);

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
                            // If majority said ok, write back the report before returning
                            trySendLocationReport(response.getUserLocationReport().getReport());
                            UserLocationReport report = response.getUserLocationReport();
                            System.out.println("Client " + userId + " was at location " + report.toString() + " at epoch " + epoch);
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

    private void requestUsersAtLocation(int x, int y, int epoch) throws IOException, SecurityException {
        UsersAtLocationQuery query = new UsersAtLocationQuery(x, y, epoch);
        UsersAtLocationRequest request = new UsersAtLocationRequest(query);

        JsonObject responseJson = readOperationsToServers(request, null, epoch);

        try {
            System.out.println("Got response " + responseJson);

            MessageType type = ContractMessage.getMessageType(responseJson);

            switch (type) {
                case USERS_AT_LOCATION_RESPONSE:
                    UsersAtLocationResponse response = UsersAtLocationResponse.fromJson(responseJson);
                    switch (response.getStatus()) {
                        case OK:
                            UsersAtLocationReport report = response.getUsersAtLocationReport();
                            System.out.println("Found users:");
                            report.getUsersAndProofs().forEach((user, v) -> System.out.println("User " + user));
                            break;
                        case NO_INFORMATION_FOR_QUERY:
                            System.out.println("Found no users");
                            break;
                        default:
                            System.out.println("Bad response status");
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

    private JsonObject readOperationsToServers(ContractMessage request, String userId, int queryEpoch) {
        ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses = new ConcurrentHashMap<>();
        List<Thread> ongoingConnectionsToServers = new LinkedList<>();

        for (int i = 1; i <= HelperConstants.NUM_SERVERS; i++) {
            String serverId = "server" + i;
            int targetPort = Utils.getServerPortFromId(serverId);
            System.out.println("Sending to Server " + i + " at port " + targetPort);
            Thread t = new Thread(() -> sendReadRequestToServer(serverId, targetPort, request, receivedResponses, userId, queryEpoch));
            t.start();
            ongoingConnectionsToServers.add(t);
        }

        // Keep waiting for answers until there is one that has a count above the byzantine quorum
        JsonObject majority;
        synchronized (readOperationLock) {
            while((majority = enoughResponses(receivedResponses)) == null) {
                try {
                    readOperationLock.wait();
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

    private void sendReadRequestToServer(String serverId, int targetPort, ContractMessage request, ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses, String userId, int queryEpoch) {
        while(true) {
            PublicKey serverPublicKey;
            try {
                serverPublicKey = this.keyPool.getPublicKey(serverId);
            } catch (KeyPoolException e) {
                System.out.println("Error getting server's public key");
                return;
            }

            try(SecureSocket serverSocket =  new SecureSocket(
                    new Socket(server_host, targetPort),
                    this.keyPool.getPublicKey(),
                    this.keyPool.getPrivateKey(),
                    serverId,
                    serverPublicKey
            )) {
                serverSocket.identify(this.id);

                serverSocket.send(request.toJson());
                JsonObject responseJson =  serverSocket.receive();
                synchronized (readOperationLock) {
                    try {
                        MessageType type = ContractMessage.getMessageType(responseJson);

                        switch (type) {
                            case OBTAIN_LOCATION_RESPONSE:
                                handleLocationResponse(responseJson, receivedResponses, userId, queryEpoch);
                                readOperationLock.notifyAll();
                                return;
                            case USERS_AT_LOCATION_RESPONSE:
                                handleUsersLocationResponse(request, responseJson, receivedResponses, queryEpoch);
                                readOperationLock.notifyAll();
                                return;
                            case ERROR_MESSAGE:
                                ErrorMessage message = ErrorMessage.fromJson(responseJson);
                                if (receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                                else receivedResponses.put(responseJson, new AtomicInteger(1));
                                readOperationLock.notifyAll();
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
            String userId,
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
                if (validateMessage(report, userId, queryEpoch)) {
                    if (receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                    else receivedResponses.put(responseJson, new AtomicInteger(1));
                }
                break;
            default:
                System.out.println("Bad Response status!");
        }
    }

    private void handleUsersLocationResponse(
            ContractMessage request,
            JsonObject responseJson,
            ConcurrentHashMap<JsonObject, AtomicInteger> receivedResponses,
            int queryEpoch) throws InvalidJsonException {

        if (request instanceof UsersAtLocationRequest) {
            UsersAtLocationRequest req = (UsersAtLocationRequest) request;

            UsersAtLocationResponse response = UsersAtLocationResponse.fromJson(responseJson);
            switch (response.getStatus()) {
                case NO_INFORMATION_FOR_QUERY:
                    if(receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                    else receivedResponses.put(responseJson, new AtomicInteger(1));
                    break;
                case OK:
                    UsersAtLocationReport report = response.getUsersAtLocationReport();
                    if(validateMessage(req, report, queryEpoch)) {
                        if(receivedResponses.containsKey(responseJson)) receivedResponses.get(responseJson).incrementAndGet();
                        else receivedResponses.put(responseJson, new AtomicInteger(1));
                    }
                    break;
                default:
                    System.out.println("Bad Response status!");
            }
        }
    }



    private void handleDefault(String command) {
        System.out.println("Unknown command " + command);
    }


    private boolean validateMessage(UserLocationReport report, String userId, int epoch) {
        // Construct the correct record
        Record record = new Record(
                userId,
                epoch,
                report.getReport().getRecord().getX(),
                report.getReport().getRecord().getY()
        );
        try {
            PublicKey clientPublicKey = this.keyPool.getPublicKey(userId);
            Proof myProof = report.getReport().getProofs().stream().filter(x -> x.getSignerId().equals(userId)).findFirst().orElseThrow(() -> new InvalidJsonException("No Proof"));

            return SecurityUtils.checkSignature(record.toString(), myProof.getSignature(), clientPublicKey);
        } catch (KeyPoolException | GeneralSecurityException | InvalidJsonException e) {
            return false;
        }
    }

    private boolean validateMessage(UsersAtLocationRequest request, UsersAtLocationReport report, int queryEpoch) {
        try {
            for (Map.Entry<String, Proof> entry: report.getUsersAndProofs().entrySet()) {
                Record record = new Record(
                        entry.getKey(),
                        queryEpoch,
                        request.getUsersAtLocationQuery().getX(),
                        request.getUsersAtLocationQuery().getY()
                );
                PublicKey userKey = this.keyPool.getPublicKey(entry.getKey());
                if (! SecurityUtils.checkSignature(record.toString(), entry.getValue().getSignature(), userKey)) return false; // End here, else continue

            }
            return true;
        } catch (KeyPoolException | GeneralSecurityException e) {
            return false;
        }


    }

    public void trySendLocationReport(Report report) {

        List<Thread> ongoingConnectionsToServers = new LinkedList<>();
        CountDownLatch receivedAcks = new CountDownLatch(Utils.getByzantineQuorum());

        // We start Ids at 1
        for (int i = 1; i <= HelperConstants.NUM_SERVERS; i++) {
            String serverId = "server" + i;
            int targetPort = Utils.getServerPortFromId(serverId);
            System.out.println("Sending to Server " + i + " at port " + targetPort);
            Thread t = new Thread(() -> sendReportToServer(serverId, targetPort, report, receivedAcks));
            t.start();
            ongoingConnectionsToServers.add(t);
        }

        try {
            receivedAcks.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            for (Thread t: ongoingConnectionsToServers) t.interrupt();
        }

    }

    private void sendReportToServer(String serverId, int serverPort, Report report, CountDownLatch receivedAcks) {
        while(true) {
            try(
                    SecureSocket socket = new SecureSocket(
                            new Socket(this.server_host, serverPort),
                            this.keyPool.getPublicKey(),
                            this.keyPool.getPrivateKey(),
                            serverId,
                            this.keyPool.getPublicKey(serverId)
                    )
            ){
                socket.identify(this.id);

                SubmitLocationRequest request = new SubmitLocationRequest(report);
                socket.send(request.toJson());

                JsonObject responseJson = socket.receive();
                // System.out.println("Got response: " + responseJson);

                MessageType type = ContractMessage.getMessageType(responseJson);
                switch(type) {
                    case SUBMIT_LOCATION_RESPONSE:
                        SubmitLocationResponse response = SubmitLocationResponse.fromJson(responseJson);

                        if (response.getStatus().equals(ResponseStatus.OK)) {
                            // Server got record, we can return
                            receivedAcks.countDown();
                            return;
                        } else {
                            System.out.printf("[epoch %d] Got NOK response from server: %s\n",
                                    report.getRecord().getEpoch(),
                                    response.getStatus().getLabel()
                            );
                        }
                        break;

                    case ERROR_MESSAGE:
                        ErrorMessage errorMessage = ErrorMessage.fromJson(responseJson);
                        System.out.printf("[epoch %d] Got error from server\n", report.getRecord().getEpoch());
                        System.out.println(errorMessage);
                        break;

                    default:
                        System.out.printf("[epoch %d] Got unexpected response from server\n", report.getRecord().getEpoch());
                }

            } catch (InvalidJsonException | IOException | KeyPoolException e) {
                //System.out.println("ERRRRORRRRRR");
            } catch (GeneralSecurityException e) {
                System.err.printf("[epoch %d] Received insecure message. Ignoring\n");
            } catch (IdentityException e) {
                System.out.println("ERROR: Could not prove identity");
            }
        }
    }


    private void showHelp() {
        System.out.print("User commands:\n" +
                "help                                | this screen\n" +
                "exit                                | leave application\n" +
                "locationReport [userId] [epoch]     | Show location of given userId for given epoch\n" +
                "usersAtLocation (x,y) [epoch]       | Show list of users at position (x,y) for given epoch\n" + "\n");
    }

    private String getGroupFromPattern(String command, Pattern pattern, int index) {
        Matcher m = pattern.matcher(command);
        m.find();
        return m.group(index);
    }

}
