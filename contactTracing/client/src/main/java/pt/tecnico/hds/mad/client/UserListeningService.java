package pt.tecnico.hds.mad.client;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import pt.tecnico.hds.mad.client.exceptions.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.*;

public class UserListeningService implements Runnable{

    private SecureServerSocket socket;
    private User user;
    private Grid grid;

    public UserListeningService(User user, int port){
        try {
            this.user = user;
            this.grid = user.getGrid();

            this.socket = new SecureServerSocket(this.user.getKeyPool(), port, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        System.out.println("Running TCP Listener");
        System.out.println("Listening at port " + this.socket.getLocalPort());
        try {
            while(true) {
                SecureSocket clientSocket = this.socket.accept();

                new Thread(() -> {
                    try {
                        handleRequest(clientSocket);
                    } catch (IOException | InvalidLocationProofResponseException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleRequest(SecureSocket clientSocket) throws InvalidLocationProofResponseException, IOException {
        try {
            ContractMessage response;
            try {
                JsonObject requestJson = clientSocket.receive();
                LocationProofRequest request = LocationProofRequest.fromJson(requestJson);
                Proof proof = this.proveRecord(request.getRecord());
                response = new LocationProofResponse(true, proof);

            } catch (InvalidJsonException | InvalidRecordException | UserException e) {
                System.err.println("Error. Sending rejected response");
                response = new LocationProofResponse(false, null);

            } catch (GeneralSecurityException e) {
                response = new ErrorMessage(ErrorMessage.SECURITY_ERROR);
            }

            clientSocket.send(response.toJson());
        } catch (GeneralSecurityException e) {
            System.err.println("Security exception while sending message...");
        }
    }

    Proof proveRecord(Record record) throws InvalidRecordException, UserException, GeneralSecurityException {
        int recordEpoch = record.getEpoch();
        String senderId = record.getUserId();
        Position pos = new Position(record.getX(), record.getY());

        try {
            if(!this.grid.getUserPosition(recordEpoch, senderId).equals(pos)) {
                throw new InvalidRecordException(InvalidRecordException.INVALID_POSITION);
            }
            if(!this.grid.isNearAtEpoch(recordEpoch, this.user.getId(), senderId)) {
                throw new InvalidRecordException(InvalidRecordException.TOO_FAR);
            }
        } catch (InvalidEpochException e) {
            throw new InvalidRecordException(InvalidRecordException.INVALID_EPOCH);
        } catch (NoSuchUserException e) {
            throw new InvalidRecordException(InvalidRecordException.INVALID_USER);
        }

        // Sign
        PrivateKey key = this.user.getKeyPool().getPrivateKey();
        String b64sig = SecurityUtils.sign(record.toString(), key);

        return new Proof(this.user.getId(), b64sig);
    }
}
