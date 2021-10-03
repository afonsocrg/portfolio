package pt.tecnico.hds.mad.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.*;
import java.security.GeneralSecurityException;

import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.server.exceptions.DatabaseAccessException;
import pt.tecnico.hds.mad.server.exceptions.*;

public class Server implements Broadcaster {
    private String myId;
    private Database db;
    private KeyPool keyPool;
    private int port;

    private Map<String, DoubleEchoBroadcast> ongoingBroadcasts;

    public Server(String myId, Database db, KeyPool keyPool) {
        this.myId = myId;
        this.db = db;
        this.keyPool = keyPool;
        this.port = Utils.getServerPortFromId(this.myId);

        this.ongoingBroadcasts = new HashMap();
    }

    public String getId() { return this.myId; }

    public void run() {
        SecureServerSocket serverSocket;
        try {
            serverSocket = new SecureServerSocket(this.keyPool, this.port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while(true) {
            try {
                SecureSocket clientSocket = serverSocket.accept();

                new Thread() {
                    public void run() {
                        handleClient(clientSocket);
                    }
                }.start();

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    // handle broadcasted messages
    public void deliverMessage(JsonObject jsonRequest) {
        try {
            MessageType type = ContractMessage.getMessageType(jsonRequest);
            switch(type) {
                case SUBMIT_LOCATION_REQUEST:
                    SubmitLocationRequest submitLocationRequest = SubmitLocationRequest.fromJson(jsonRequest);
                    this.handleSubmitLocation(submitLocationRequest);
                    break;
                default:
                    break;
            }
        } catch(IOException | KeyPoolException | InvalidJsonException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(SecureSocket clientSocket) {
        try {
            try {
                ContractMessage response = null;
                JsonObject jsonRequest = clientSocket.receive();

                MessageType type = ContractMessage.getMessageType(jsonRequest);
                switch(type) {
                    case BROADCAST_MESSAGE:
                        BroadcastMessage bm = BroadcastMessage.fromJson(jsonRequest);
                        if(bm.getStatus() == BroadcastStatus.SEND) {
                            // only ask for pow if sending
                            clientSocket.receiveExpensive(jsonRequest);
                        }

                        // MAROSCA: indexing broadcasts by <userId:epoch>
                        // we know the content will be a SubmitLocationRequest.
                        // if it is not, we don't care about this msg in particular
                        JsonObject msgJson = bm.getMessage();
                        SubmitLocationRequest msg = SubmitLocationRequest.fromJson(msgJson);
                        String s = msg.getReport().getRecord().getUserId();
                        int epoch = msg.getReport().getRecord().getEpoch();
                        String broadcastId = s + ":" + epoch;

                        this.ongoingBroadcasts.putIfAbsent(
                            broadcastId,
                            new DoubleEchoBroadcast(s, this, this.keyPool)
                        );

                        this.ongoingBroadcasts.get(broadcastId).handleMessage(bm, clientSocket);

                        break;

                    case OBTAIN_LOCATION_REQUEST:
                        ObtainLocationRequest obtainLocationRequest = ObtainLocationRequest.fromJson(jsonRequest);
                        response = this.handleObtainLocation(
                            obtainLocationRequest,
                            clientSocket.getRemoteId()
                        );
                        break;

                    case USERS_AT_LOCATION_REQUEST:
                        UsersAtLocationRequest usersAtLocationRequest = UsersAtLocationRequest.fromJson(jsonRequest);
                        response = this.handleUsersAtLocation(usersAtLocationRequest);
                        break;

                    case MY_PROOFS_REQUEST:
                        MyProofsRequest myProofsRequest = MyProofsRequest.fromJson(jsonRequest);
                        response = this.handleRequestMyProofs(
                                myProofsRequest,
                                clientSocket.getRemoteId()
                        );
                        break;

                    default:
                        response = this.handleDefault(jsonRequest);
                        break;
                }
                if(response != null) {
                    clientSocket.send(response.toJson());
                }
            } catch (JsonParseException 
                    | InvalidJsonException e) {
                clientSocket.send((new ErrorMessage(ErrorMessage.INVALID_REQUEST_FORMAT)).toJson());
            } catch(ProofOfWorkException e) {
                System.err.printf("PoW: %s%n", e.getMessage());
            } catch (InvalidObtainLocationResponseException e) {
                e.printStackTrace();
                clientSocket.send((new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR)).toJson());
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public ContractMessage handleSubmitLocation(SubmitLocationRequest request)
        throws IOException, KeyPoolException
    { 

        try {
            Report report = request.getReport();

            if(!validateReport(report)) {
                return new SubmitLocationResponse(ResponseStatus.INSUFFICIENT_EVIDENCE);
            }

            System.out.printf(
                "User %s submitted location (%d, %d) at epoch %d%n",
                report.getUserId(),
                report.getRecord().getX(),
                report.getRecord().getY(),
                report.getRecord().getEpoch()
            );

            this.saveReport(report);
            return new SubmitLocationResponse(ResponseStatus.OK);

        } catch (DatabaseAccessException e) {
            e.printStackTrace();
            return new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: Get from the Socket what type of user are we talking to, normal or HA
    public ContractMessage handleObtainLocation(ObtainLocationRequest request, String remoteId) throws InvalidObtainLocationResponseException {
        System.out.println("User requested location:");
        System.out.println(request.toJson().toString());

        try {
            UserLocationQuery query = request.getUserLocationQuery();
            String queryUserId = query.getUserId();
            int epoch = query.getEpoch();

          if(!validIdRequest(remoteId ,queryUserId)) {
              return new ObtainLocationResponse(ResponseStatus.INVALID_ID_REQUEST, null);
          }

            Record record = this.db.getRecord(queryUserId, epoch);
            List<Proof> proofs = this.db.getProofs(queryUserId, epoch);
            UserLocationReport report = new UserLocationReport(new Report(record, proofs));

            return new ObtainLocationResponse(ResponseStatus.OK, report);
        } catch (DatabaseAccessException | DatabaseTooManyResultsException e) {
            e.printStackTrace();
            return new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        } catch (DatabaseNotFoundException e) {
            return new ObtainLocationResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null);
        }
    }

    public ContractMessage handleUsersAtLocation(UsersAtLocationRequest request) {
        System.out.println("HA requested users at: ");
        System.out.println(request.toJson().toString());

        try {
            UsersAtLocationQuery query = request.getUsersAtLocationQuery();
            int x = query.getX();
            int y = query.getY();
            int epoch = query.getEpoch();

            List<Record> recordList = this.db.getRecords(x, y, epoch);
            Map<String, Proof> idsAndProofs = new HashMap<>();

            for(Record record: recordList) {
                idsAndProofs.put(record.getUserId(), this.db.getProof(record.getUserId(), epoch, record.getUserId()));
            }

            return idsAndProofs.keySet().isEmpty() ?
                    new UsersAtLocationResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null) :
                    new UsersAtLocationResponse(ResponseStatus.OK, new UsersAtLocationReport(idsAndProofs));

        } catch (DatabaseAccessException | DatabaseTooManyResultsException e) {
            e.printStackTrace();
            return new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        } catch (DatabaseNotFoundException e) {
            return new UsersAtLocationResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null);
        }
    }

    public ContractMessage handleRequestMyProofs(MyProofsRequest request, String remoteId) {
        System.out.println("User requested proofs of epochs: ");
        System.out.println(request.toJson().toString());

        try {
            MyProofsQuery query = request.getMyProofsQuery();
            String queryUserId = query.getUserId();
            List<Integer> epochs = query.getEpochs();

            if (!queryUserId.equals(remoteId)) {
                return new MyProofsResponse(ResponseStatus.INVALID_ID_REQUEST, null);
            }

            List<RecordProofPair> pairs = new ArrayList<>();
            for (Integer epoch: epochs) {
                try {
                    pairs.addAll(this.db.getProofsOfUser(queryUserId, epoch));
                } catch (DatabaseNotFoundException e) {
                    // If there is no proof for a certain epoch there is no problem
                }
            }

            MyProofsReport report = new MyProofsReport(pairs);
            return pairs.isEmpty() ? new MyProofsResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, null) : new MyProofsResponse(ResponseStatus.OK, report);
        } catch (DatabaseAccessException e) {
            e.printStackTrace();
            return new ErrorMessage(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
    }

    public ContractMessage handleDefault(JsonObject request) { 
        System.out.println("Unknown request");
        return new ErrorMessage(ErrorMessage.UNKNOWN_REQUEST);
    }

    public boolean validateReport(Report report) throws KeyPoolException {
        Record record = report.getRecord();
        Set<String> signers = new HashSet();

        Iterator<Proof> it = report.getProofs().iterator();
        while(it.hasNext()) {
            Proof proof = it.next();

            String signerId = proof.getSignerId();

            try {
                if(proof.isValid(record, this.keyPool.getPublicKey(signerId)) &&
                    !signers.contains(signerId)) {

                    signers.add(signerId);
                } else {
                    // ignoring invalid proofs
                    it.remove();
                }
            } catch (GeneralSecurityException e) {
                // failed to check proof. it is invalid!
                it.remove();
            }
        }

        // the requester proved its record (cannot repudiate its location)
        return signers.contains(record.getUserId())
            // at least one extra correct user approved the record (extra user + requester)
            && signers.size() >= Utils.getRequiredProofsCount() + 1; // +1 is requester
    }


    private boolean validIdRequest(String remoteId, String queryId) {
        return remoteId.contains("ha") || remoteId.equals(queryId);
    }


    public void saveReport(Report report) throws DatabaseAccessException {
        try {
            this.db.setReport(report);
        } catch (DatabaseDuplicateRecordException e) {
            // tried to add duplicate record. It's okay, do nothing...
            System.out.println("Received duplicate report. Ignoring...");
        } catch (DatabaseAccessException e) {
            System.out.println("Got error: " + e.getClass());
            throw e;
        }
    }
}
