import java.io.IOException;

public class App {
    // Must be run as root: https://groups.google.com/g/bluecove-users/c/EbsDg7rpdTM/m/TTd9vGo0PfEJ
    
    private static final String serviceName = "BlueGateServer";
    private static final String uuid = "4db6bd94275411eb8443b827eb2a5a73";

    public static void main(String[] args) {
        try {
            BluetoothServer server = new BluetoothServer();
            BlueGateService blueGateService = new BlueGateService(uuid, serviceName);

            server.runService(blueGateService);

            try {
                System.out.println("Press enter to stop bluetooth server");
                System.in.read();
            } catch(IOException e) {
                System.err.println("Error while waiting for user input");
                return;
            }

            server.shutdown();

        } catch(BluetoothException e) {
            System.err.println(e.getMessage());
        }
    }
}
