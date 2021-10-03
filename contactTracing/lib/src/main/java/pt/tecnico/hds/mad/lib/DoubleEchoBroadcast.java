package pt.tecnico.hds.mad.lib.security;

import com.google.gson.*;
import pt.tecnico.hds.mad.lib.*;
import pt.tecnico.hds.mad.lib.contract.*;
import pt.tecnico.hds.mad.lib.security.*;
import pt.tecnico.hds.mad.lib.exceptions.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.*;
import java.util.*;
import java.security.*;


public class DoubleEchoBroadcast {

    private static final int N = HelperConstants.NUM_SERVERS;
    private static final int f = HelperConstants.MAX_BYZANTINE_SERVERS;

    private JsonObject msg;

    private String s;

    // Entity that created this object
    // used to callback delivered messages
    private Broadcaster me;
    private KeyPool keyPool;

    // id of process that initiated the broadcast
    // This field is a list since byzantine processes
    // may send SEND messages. We want to send our READY
    // to the real sender, so we better send it to everyone
    // that sent us a SEND
    private Set<SecureSocket> sendingSockets;

    private AtomicBoolean sentEcho;
    private AtomicBoolean sentReady;
    private AtomicBoolean delivered;
    private JsonObject deliveredMsg;
    
    private Map<String, Set<String>> echos;
    private Map<String, Set<String>> readies;

    private Map<String, Thread> ongoingSends;
    private Map<String, Thread> ongoingEchos;
    private Map<String, Thread> ongoingReadies;

    private CountDownLatch sendingLatch;

    // Receives the message it is broadcasting
    public DoubleEchoBroadcast(String s, Broadcaster me, KeyPool keyPool) {
        // process that started the broadcast
        this.s = s;

        this.me = me;

        this.keyPool = keyPool;

        this.sentEcho = new AtomicBoolean(false);
        this.sentReady = new AtomicBoolean(false);
        this.delivered = new AtomicBoolean(false);
        this.deliveredMsg = null;

        this.echos = new ConcurrentHashMap();
        this.readies = new ConcurrentHashMap();

        this.ongoingSends = new ConcurrentHashMap();
        this.ongoingEchos = new ConcurrentHashMap();
        this.ongoingReadies = new ConcurrentHashMap();

        this.sendingSockets = new HashSet();
    }

    // this method blocks until it has received a quorum of READYs
    public void broadcast(JsonObject msg) {
        if(!this.s.equals(this.me.getId())) {
            // only issuer can broadcast
            return;
        }

        // has to be greater than 2f, so we add 1
        this.sendingLatch = new CountDownLatch(2*f + 1);

        try {
            BroadcastMessage bcastMsg = new BroadcastMessage(msg, BroadcastStatus.SEND);

            // System.out.println("DEB starting broadcast");
            for (int i = 1; i <= HelperConstants.NUM_SERVERS; i++) {
                String targetId = "server" + i;
                Thread t = new Thread(() -> sendAndWait(bcastMsg, targetId));
                t.start();
                ongoingSends.put(targetId, t);
            }

            // wait for the quorum of READYs
            this.sendingLatch.await();

            for(Thread t : this.ongoingSends.values()) {
                if(t.isAlive()) t.interrupt();
            }
            this.ongoingSends.clear();
        } catch(InterruptedException e) {
            return;
        }
    }

    public void handleMessage(BroadcastMessage bcastMsg, SecureSocket socket) {
        BroadcastStatus status = bcastMsg.getStatus();
        // System.out.println("Received " + status.getLabel() + " from " + socket.getRemoteId());
        switch(status) {
            case SEND:
                receivedSend(bcastMsg, socket);
                break;
            case ECHO:
                receivedEcho(bcastMsg, socket);
                break;
            case READY:
                receivedReady(bcastMsg, socket);
                break;
            default:
                // ignore
        }
    }

    private void receivedSend(BroadcastMessage bcastMsg, SecureSocket socket) {
        // save possible sender
        String p = socket.getRemoteId();

        // if received send from someone else than
        // original sender, discard
        if(!p.equals(this.s)) {
            return;
        }

        this.sendingSockets.add(socket);

        if(!this.sentEcho.getAndSet(true)) {
            this.sendEcho(bcastMsg.getMessage());
            return;
        }

        // if delivered, send ready
        if(this.delivered.get()) {
            // System.out.println("Already delivered. sending READY to SENDer");
            BroadcastMessage ready = new BroadcastMessage(this.deliveredMsg, BroadcastStatus.READY);
            try {
                // we only need to send once since a correct sender
                // will send infinitely until if has received a 
                // quorum of READYs. if this message is lost, we will
                // receive another SEND from the same correct sender
                socket.send(ready.toJson());
            } catch(IOException | GeneralSecurityException e) {
                // cancel send
            }
        }
    }

    private void receivedEcho(BroadcastMessage bcastMsg, SecureSocket socket) {
        String p = socket.getRemoteId();

        String strMsg = bcastMsg.getMessage().toString();
        this.echos.putIfAbsent(strMsg, new HashSet());

        // add p to processes that echoed msg
        if(this.echos.get(strMsg).contains(p)) return;
        this.echos.get(strMsg).add(p);

        // IMPORTANT to keep this condition order:
        // if the size is not enough we don't want to set the
        // flag to true, but if it is, we want to set it atomically
        if(this.echos.get(strMsg).size() > (N + f)/2 && !this.sentReady.getAndSet(true)) {
            this.sendReady(bcastMsg.getMessage());
        }
    }

    private void receivedReady(BroadcastMessage bcastMsg, SecureSocket socket) {
        String p = socket.getRemoteId();

        String strMsg = bcastMsg.getMessage().toString();
        this.readies.putIfAbsent(strMsg, new HashSet());
        if(this.readies.get(strMsg).contains(p)) return;

        this.readies.get(strMsg).add(p);

        // stop sending echos to this process
        Thread t = this.ongoingEchos.remove(p);
        if(t != null) {
            if(t.isAlive()) t.interrupt();
        }

        // amplification step
        // IMPORTANT to keep this condition order:
        // if the size is not enough we don't want to set the
        // flag to true, but if it is, we want to set it atomically
        if(this.readies.get(strMsg).size() > f && !this.sentReady.getAndSet(true)) {
            this.sendReady(bcastMsg.getMessage());
        }

        // check delivery
        // IMPORTANT to keep this condition order:
        // if the size is not enough we don't want to set the
        // flag to true, but if it is, we want to set it atomically
        if(this.readies.get(strMsg).size() > 2*f && !this.delivered.getAndSet(true)) {
            this.deliver(bcastMsg.getMessage());
        }
    }

    

    private void sendEcho(JsonObject msg) {
        // System.out.println("Sending ECHO");

        String strMsg = msg.toString();
        this.echos.putIfAbsent(strMsg, new HashSet());
        this.echos.get(strMsg).add(this.me.getId());

        BroadcastMessage bcastMsg = new BroadcastMessage(msg, BroadcastStatus.ECHO);
        for (int i = 1; i <= HelperConstants.NUM_SERVERS; i++) {
            String targetId = "server" + i;
            Thread t = new Thread(() -> sendMessage(bcastMsg, targetId));
            t.start();
            ongoingEchos.put(targetId, t);
        }
    }

    private void sendReady(JsonObject msg) {
        // System.out.println("Sending READY");

        String strMsg = msg.toString();
        this.readies.putIfAbsent(strMsg, new HashSet());
        this.readies.get(strMsg).add(this.me.getId());
        
        BroadcastMessage bcastMsg = new BroadcastMessage(msg, BroadcastStatus.READY);
        for (int i = 1; i <= HelperConstants.NUM_SERVERS; i++) {
            String targetId = "server" + i;
            Thread t = new Thread(() -> sendMessage(bcastMsg, targetId));
            t.start();
            ongoingReadies.put(targetId, t);
        }
    }

    private void deliver(JsonObject msg) {
        this.deliveredMsg = msg;

        // Stopping sending echos/readies may break the 
        // totality property of DEB

        // send ready to initiators
        BroadcastMessage ready = new BroadcastMessage(msg, BroadcastStatus.READY);
        for(SecureSocket s : this.sendingSockets) {
            try {
                // we only need to send once since a correct sender
                // will send SEND infinitely until it has received a 
                // quorum of READYs. if this message is lost, we will
                // receive another SEND from the same correct sender
                s.send(ready.toJson());
            } catch(IOException | GeneralSecurityException e) {
                // ignore...
            }
        }

        this.me.deliverMessage(msg);
    }


    private void sendMessage(BroadcastMessage m, String to) {
        int targetPort = Utils.getServerPortFromId(to);
        while(true) {
            try (
                SecureSocket socket = new SecureSocket(
                    new Socket(InetAddress.getLocalHost(), targetPort),
                    this.keyPool.getPublicKey(),
                    this.keyPool.getPrivateKey(),
                    to,
                    this.keyPool.getPublicKey(to)
                )
            ) {

                socket.identify(this.me.getId());
                // System.out.println("Sending " + m.getStatus().getLabel() + " to " + to);
                socket.send(m.toJson());
                Thread.sleep(1000);

            } catch (IOException | GeneralSecurityException e) {
                // do nothing: Could not establish connection. Will retry now
            } catch (KeyPoolException e) {
                System.out.println("ERROR: Can't retrieve public key from user: " + e.getKeyId());
            } catch (IdentityException e) {
                System.out.println("ERROR: Could not prove identity");
            } catch(InterruptedException e) {
                // stop sending
                break;
            }
        }
    }

    private void sendAndWait(BroadcastMessage  msg, String to) {
        int targetPort = Utils.getServerPortFromId(to);
        while(true) {
            try (
                SecureSocket socket = new SecureSocket(
                    new Socket(InetAddress.getLocalHost(), targetPort),
                    this.keyPool.getPublicKey(),
                    this.keyPool.getPrivateKey(),
                    to,
                    this.keyPool.getPublicKey(to)
                )
            ) {

                socket.identify(this.me.getId());
                do {
                    socket.sendExpensive(msg.toJson());
                    Thread.sleep(1000);
                } while(!socket.hasBytes());

                JsonObject response = socket.receive();
                MessageType type = ContractMessage.getMessageType(response);
                if(
                    type == MessageType.BROADCAST_MESSAGE && 
                    BroadcastMessage.fromJson(response).getStatus() == BroadcastStatus.READY &&
                    BroadcastMessage.fromJson(response).getMessage().toString().equals(msg.toString())
                ) {
                    System.out.println(to + " delivered");
                    break;
                }
            } catch (IOException | GeneralSecurityException | ProofOfWorkException e) {
                // do nothing: Could not establish connection. Will retry now
            } catch (KeyPoolException e) {
                System.out.println("ERROR: Can't retrieve public key from user: " + e.getKeyId());
            } catch (IdentityException e) {
                System.out.println("ERROR: Could not prove identity");
            } catch(InterruptedException | InvalidJsonException e) {
                // stop sending (signature valid and json invalid -> byzantine peer
                return;
            }
        }

        this.ongoingSends.remove(to);
        this.sendingLatch.countDown();
    }
}
