package pt.tecnico.hds.mad.client;

import java.io.*;

import pt.tecnico.hds.mad.client.exceptions.UserException;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;
import pt.tecnico.hds.mad.lib.security.*;

public class ClientApp {
    private static final String STORE_PATH = "../global.keystore.jks";
    private static final String STORE_PASS = "globalpass";
    private static final String KEY_PASS_SUFFIX = "pass";

    public static void main(String[] args) {
        String keyStorePath = STORE_PATH;

        // command line args
        // id, board_config_file
        if (args.length < 2) {
            System.out.println("Missing arguments");
            System.out.println("Usage java " + ClientApp.class.getName() + " <id> <board_config_file> [-k <keystore>]");
        }

        String id = "client" +  args[0]; // To keep the calling arguments the same, can change if we want

        String gridConfigFile = args[1];

        for (int i = 2; i < args.length; i++) {
            if (args[i] == "-k" && i+1 < args.length) {
                keyStorePath = args[++i];
            }
        }

        try {
            // create Grid from the config file
            Grid grid = new Grid(gridConfigFile);

            String keyId = id;
            String keyPass = id + KEY_PASS_SUFFIX;
            KeyPool keyPool = new KeyPool(keyStorePath, STORE_PASS, keyId, keyPass);

            User user = new User(id, grid, keyPool);
            user.start();
        } catch (UserException | IOException e) {
            e.printStackTrace();
        } catch (KeyPoolException e) {
            System.err.println("Couldn't create keypool");
            System.exit(1);
        }
    }
}
