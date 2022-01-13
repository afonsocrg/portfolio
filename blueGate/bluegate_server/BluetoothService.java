import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

import java.util.Set;
import java.util.HashSet;

public abstract class BluetoothService {

    /**
     * The service name
     */
    private String name;

    /**
     * The service UUID
     */
    private UUID uuid;

    /**
     * The service conncetion URL
     */
    private String connectionURL;

    /**
     * The service listener
     */
    private StreamConnectionNotifier streamConnectionNotifier;

    /**
     * Stored client connections
     */
    private Set<StreamConnection> clientConnections;


    /**
     * Create new Bluetooth service
     * @param uuid The service UUID
     * Name defaults to ""
     */
    public BluetoothService(String uuid) throws BluetoothException {
        this(uuid, "");
    }

    /**
     * Create new Bluetooth service
     * @param uuid The service UUID
     * @param name The service name
     */
    public BluetoothService(String uuid, String name) throws BluetoothException {
        this.name = name;

        // set UUID
        try {
            this.uuid = new UUID(uuid, false);
        } catch (NumberFormatException e) {
            throw new BluetoothException("BluetoothService: given uuid has non hexadecimal characters");
        } catch (IllegalArgumentException e) {
            throw new BluetoothException("BluetoothService: given uuid has invalid length");
        }


        // Set connectionURL (using RFCOMM protocol)
        this.connectionURL = "btspp://localhost:" + this.uuid;
        if(this.name != "") { this.connectionURL += ";name="+name; }

        try {
            this.streamConnectionNotifier = (StreamConnectionNotifier) Connector.open(this.connectionURL);
        } catch (IllegalArgumentException e) {
            throw new BluetoothException("BluetoothService: Invalid connection url");
        } catch (ConnectionNotFoundException e) {
            throw new BluetoothException("BluetoothService: Protocol not supported");
        } catch (IOException e) {
            throw new BluetoothException("BluetoothService: IO Error");
        } catch (SecurityException e) {
            throw new BluetoothException("BluetoothService: Could not access protocol handler (Permission denied)");
        }

        this.clientConnections = new HashSet<StreamConnection>();
        System.out.println("Created service: " + this.connectionURL);
    }

    /**
     * @return The service UUID
     */
    public String getUUID() { return this.uuid.toString(); }

    /**
     * @return The service name
     */
    public String getName() { return this.name; }


    /**
     * Stop the service
     * Closes the listener every open client connection
     */
    public void stop() {
        // close service listener
        try {
            this.streamConnectionNotifier.close();
        } catch (IOException e) {
            // Error while closing... should be fine
        }


        // close active client connections
        for(StreamConnection connection : this.clientConnections) {
            System.out.println("Closing client connection");
            try {
                connection.close();
            } catch (IOException e) {
                // Error while closing... should be fine
            }
        }

    }


    /**
     * Starts the Bluetooth service
     * The listener starts in a new thread
     * When a new client connection is established,
     * it is handled in another thread
     */
    public void start() throws BluetoothException {
        System.out.println("Starting service " + this.name);

        // Start listening in a new thread (don't block caller)
        new Thread() {
            public void run() {
                try {
                    while(true) {
                        System.out.println("Waiting for clients...");
                        StreamConnection connection = BluetoothService.this.streamConnectionNotifier.acceptAndOpen();

                        // add connection to connection set
                        // need to be synchronized since serving threads
                        // may alter this datastructure when exiting
                        synchronized(this) {
                            BluetoothService.this.clientConnections.add(connection);
                        }

                        // start handling client in new thread
                        new Thread() {
                            public void run() {
                                try {
                                    handle_client(connection);
                                    // remove connection from connection set
                                    synchronized(this) {
                                        BluetoothService.this.clientConnections.remove(connection);
                                    }
                                    connection.close();
                                } catch (IOException e) {
                                    System.err.println("Error while handling client: connection error");
                                }
                            }
                        }.start();
                    }
                } catch(IOException e) {
                    System.err.println("IO error while serving client");
                }
            }
        }.start();
    }

    /**
     * Client connection handler
     * Subclasses must implement this handler to define their behaviour
     * @param connection The client connection
     * @throws IOException if the connection fails
     */
    public abstract void handle_client(StreamConnection connection) throws IOException;
}
