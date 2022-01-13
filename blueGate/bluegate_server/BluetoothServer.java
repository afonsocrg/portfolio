import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;
import java.util.Map;
import java.util.HashMap;

public class BluetoothServer {

    /**
     * Initial bluetooth discoverable mode (restoring it when exiting)
     */
    private int initialDiscoverableMode;

    /**
     * Local bluetooth device
     */
    private LocalDevice localDevice;

    /**
     * Collection of running services
     */
    private Map<String, BluetoothService> runningServices;


    /**
     * Initializes Bluetooth server
     * Stores initial discoverable mode
     * Set discoverable mode to GIAC
     */
    public BluetoothServer() throws BluetoothException {
        System.out.println("Initializing bluetooth server...");

        this.runningServices = new HashMap<String, BluetoothService>();


        // Set up bluetooth discoverable mode
        try {
            this.localDevice = LocalDevice.getLocalDevice();
        } catch (BluetoothStateException e) {
            throw new BluetoothException("getLocalDevice(): Bluetooth system could not be initialized");
        }

        int newDiscoverableMode = DiscoveryAgent.GIAC;
        this.initialDiscoverableMode = this.localDevice.getDiscoverable();
        System.out.println("Initial Bluetooth state: " + this.initialDiscoverableMode);
        System.out.println("Setting Bluetooth state: " + newDiscoverableMode);
        setDiscoverableMode(newDiscoverableMode);

        String address = this.localDevice.getBluetoothAddress();
        String name = this.localDevice.getFriendlyName();
        System.out.println("Starting Bluetooth server with address " + address + " (" + name + ")");
    }


    /**
     * Cleanly stops Bluetooth server
     * 1. Stops every running service
     * 2. Restores initial discoverable mode
     */
    public void shutdown() throws BluetoothException {
        System.out.println("Exiting...");

        // stop every active service
        for(String uuid : this.runningServices.keySet()) {
            System.out.println("Stopping service " + uuid);
            stopService(uuid);
        }


        // reset bluetooth state
        System.out.println("Restoring initial Bluetooth state: " + this.initialDiscoverableMode);
        setDiscoverableMode(DiscoveryAgent.NOT_DISCOVERABLE);
    }


    /**
     * Starts given bluetooth service
     * @param service The service to start
     */
    public void runService(BluetoothService service) throws BluetoothException {

        String serviceUUID = service.getUUID();
        if(this.runningServices.containsKey(serviceUUID)) {
            System.out.println("Could not add service: There already exists one service running with the same UUID");
            return;
        }

        service.start();
        this.runningServices.put(service.getUUID(), service);
    }


    /**
     * Stops given bluetooth service
     * @param uuid The UUID of the service to stop
     */
    public void stopService(String uuid) {
        BluetoothService service = this.runningServices.get(uuid);
        if(service == null) return; // no such service
        service.stop();
    }


    /**
     * Sets bluetooth discoverable mode.
     * @param targetMode new discoverable mode.
     */
    private void setDiscoverableMode(int targetMode) throws BluetoothException {
        int currentMode = this.localDevice.getDiscoverable();
        if(currentMode == targetMode) { return; }

        try {
            this.localDevice.setDiscoverable(targetMode);
        } catch (IllegalArgumentException e) {
            throw new BluetoothException("setDiscoverable(): Invalid discoverable mode");
        } catch (BluetoothStateException e) {
            throw new BluetoothException("setDiscoverable(): Cannot change discoverable mode in current Bluetooth state");
        }
    }
}
