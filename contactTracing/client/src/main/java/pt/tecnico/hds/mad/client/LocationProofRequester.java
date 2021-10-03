package pt.tecnico.hds.mad.client;

import com.google.gson.JsonObject;
import java.security.GeneralSecurityException;
import pt.tecnico.hds.mad.client.exceptions.*;
import pt.tecnico.hds.mad.lib.Utils;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.exceptions.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.HelperConstants;

import java.io.IOException;
import java.net.*;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LocationProofRequester implements Runnable {

    private LocationProofManager manager;
    private User user;
    private PrivateKey myKr;
    private PublicKey myKu;

    private Record myRecord;
    private LocationProofRequest myRequest;

    private List<GridUser> nearMe;

    private ConcurrentLinkedQueue<Proof> proofs = new ConcurrentLinkedQueue<>();

    private List<Thread> ongoingProofRequests = new LinkedList<>();

    // 1. create my record
    // 2. find near users
    public LocationProofRequester(LocationProofManager manager, Record record)
        throws SocketException, GeneralSecurityException, InvalidEpochException {

        this.manager = manager;
        this.user = manager.getUser();
        this.myKr = this.user.getKeyPool().getPrivateKey();
        this.myKu = this.user.getKeyPool().getPublicKey();

        this.myRecord = record;
        this.myRequest = new LocationProofRequest(this.myRecord);

        String b64sig = SecurityUtils.sign(record.toString(), this.myKr);
        this.proofs.add(new Proof(record.getUserId(), b64sig));

        this.nearMe = this.user.getGrid().whoIsNearAtEpoch(this.myRecord.getEpoch(), this.user.getId());
    }


    // 3. launch a thread for each user and keep it stored
    // 4. in each thread send the record and wait for a proof
    // 5. When thread sees enough proofs (f' + 1) it sends them to the server
    // 6. If server response is valid, kill all waiting threads
    @Override
    public void run() {
        if (nearMe.size() < HelperConstants.MAX_BYZANTINE_USERS + 1) {
            // If there are less nearby neighbors than required, there is no need to ask for proofs
            return;
        }

        for (GridUser gridUser: nearMe) {
            int targetPort = Utils.getListenerServicePortFromId(gridUser.getId());

            Thread t = new Thread(() -> sendRecordAndReceive(myRequest, gridUser, targetPort));
            t.start();
            ongoingProofRequests.add(t);
        }

        try {
            for (Thread t: ongoingProofRequests) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            for (Thread t: ongoingProofRequests) t.interrupt();
        }
    }

    private synchronized void trySendToServer() throws SecurityException, KeyPoolException, IdentityException, IOException {
        // thread will be interrupted if already got OK from server
        if (Thread.currentThread().isInterrupted()) return;

        // Wait for enough proofs (don't forget we included our proof)
        if (proofs.size() >= HelperConstants.MAX_BYZANTINE_USERS + 1 + 1) {
            // Send records to all known servers
            this.user.trySendLocationReport(new Report(this.myRecord, List.copyOf(proofs)));
            // System.out.printf("[epoch %d] Got OK from quorum of servers\n", this.myRecord.getEpoch());

            // kill all threads
            for(Thread t: ongoingProofRequests) {
                // I will kill myself last
                if (t.isAlive() && Thread.currentThread().getId() != t.getId()) t.interrupt();
            }
        }
    }

    private void sendRecordAndReceive(LocationProofRequest request, GridUser gridUser, int targetPort) {
        JsonObject responseJson;
        while(true) {
            try (
                SecureSocket socket = new SecureSocket(
                    new Socket(InetAddress.getLocalHost(), targetPort),
                    this.myKu,
                    this.myKr,
                    gridUser.getId(),
                    this.getClientPublicKey(gridUser.getId()),
                    false // do not cipher these messages
                )
            ) {

                socket.identify(this.user.getId());
                socket.send(request.toJson());

                responseJson = socket.receive();

                LocationProofResponse response = LocationProofResponse.fromJson(responseJson);
                if(!response.isAccepted()) {
                    System.out.printf("[epoch %d] Rejected by %d\n", this.myRecord.getEpoch(), gridUser.getId());
                    return;
                }

                Proof proof = response.getProof();

                // check if proof is valid
                PublicKey sigKey = this.getClientPublicKey(proof.getSignerId());

                // We dont want duplicated proofs
                if(proof.isValid(request.getRecord(), sigKey) && !proofs.contains(proof)) { 
                    System.out.printf("[epoch %d] Got proof from %s\n",
                        this.myRecord.getEpoch(),
                        proof.getSignerId()
                    );
                    proofs.add(proof);
                    trySendToServer();
                } else {
                    System.out.printf("[epoch %d] Invalid proof from %s\n",
                        this.myRecord.getEpoch(),
                        proof.getSignerId()
                    );
                }
                return;
            } catch (InvalidJsonException e) {
                // ignore invalid response
                System.out.printf("[epoch %d] Got invalid response from %s\n",
                    this.myRecord.getEpoch(),
                    gridUser.getId()
                );
            } catch (IOException | GeneralSecurityException e) {
                // do nothing: Could not establish connection. Will retry now
            } catch (KeyPoolException e) {
                System.out.println("ERROR: Can't retrieve public key from user: " + e.getKeyId());
            } catch (IdentityException e) {
                System.out.println("ERROR: Could not prove identity");
            }
        }
    }



    private PublicKey getClientPublicKey(String id) throws KeyPoolException {
        return this.user.getKeyPool().getPublicKey(id);
    }
}
