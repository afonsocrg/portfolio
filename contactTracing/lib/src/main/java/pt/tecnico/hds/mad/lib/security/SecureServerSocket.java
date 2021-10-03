package pt.tecnico.hds.mad.lib.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pt.tecnico.hds.mad.lib.contract.IdentityRequest;
import pt.tecnico.hds.mad.lib.contract.IdentityResponse;
import pt.tecnico.hds.mad.lib.exceptions.IdentityException;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

public class SecureServerSocket implements AutoCloseable {
    private ServerSocket serverSocket;
    private KeyPool keyPool;
    private boolean isCiphering;

    public SecureServerSocket(KeyPool keyPool, int port, boolean isCiphering) throws IOException {
        this.keyPool = keyPool;
        this.serverSocket = new ServerSocket(port);
        this.isCiphering = isCiphering;
    }

    public SecureServerSocket(KeyPool keyPool, int port) throws IOException {
        // by default all client sockets will cipher
        this(keyPool, port, true);
    }

    public SecureSocket accept() throws IOException {
        Socket remoteSocket;
        while (true) {
            remoteSocket = this.serverSocket.accept();
            try {
                JsonObject json = JsonParser.parseString(read(remoteSocket))
                        .getAsJsonObject();

                IdentityRequest req = IdentityRequest.fromJson(json);

                String remoteId = req.getId();

                PublicKey remoteKey = getRemoteKey(remoteId);

                write(remoteSocket, IdentityResponse.Ok().toString());

                return new SecureSocket(remoteSocket,
                        this.keyPool.getPublicKey(),
                        this.keyPool.getPrivateKey(),
                        remoteId,
                        remoteKey);

            } catch (Exception e) {
                System.err.println("Failed to accept secure socket: " + e.getMessage());
                if (remoteSocket.isConnected()) {
                    write(remoteSocket, IdentityResponse.Nok(e.getMessage()).toString());
                    remoteSocket.close();
                }
            }
        }
    }

    private PublicKey getRemoteKey(String id) throws KeyPoolException {
        /* Needed while not using string IDs */
        return this.keyPool.getPublicKey(id);
    }

    private String read(Socket s) throws IOException {
        StringBuilder res = new StringBuilder();
        InputStream in = s.getInputStream();
        while (true) {
            byte b = (byte)in.read();
            if (b == -1) throw new IOException();
            if (b == 0) break; // expecting a null terminated byte array
            if (b < 0x20 || 0x7e < b) continue;
            res.append((char)b);
        }
        return res.toString();
    }

    private void write(Socket s, String msg) throws IOException {
        OutputStream out = s.getOutputStream();
        out.write(msg.getBytes());
        out.write(0);
        out.flush();
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }

    public int getLocalPort() {
        return this.serverSocket.getLocalPort();
    }
}
