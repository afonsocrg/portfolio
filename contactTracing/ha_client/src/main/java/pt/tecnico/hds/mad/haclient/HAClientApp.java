package pt.tecnico.hds.mad.haclient;


import pt.tecnico.hds.mad.lib.security.KeyPool;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;

public class HAClientApp {
    private static final String STORE_PATH = "../global.keystore.jks";
    private static final String STORE_PASS = "globalpass";
    private static final String KEY_PASS_SUFFIX = "pass";

    public static void main(String[] args) {
        String keyStorePath = STORE_PATH;

        // command line args
        // id
        if (args.length < 1) {
            System.out.println("Missing arguments");
            System.out.println("Usage: java " + HAClientApp.class.getName() + " <id> [-k <keystore>]");
            return;
        }
        String id = "ha" + args[0];

        for (int i = 1; i < args.length; i++) {
            if (args[i] == "-k" && i+1 < args.length) {
                keyStorePath = args[++i];
            }
        }

        String keyId = id;
        String keyPass = id + KEY_PASS_SUFFIX;

        try {
            KeyPool keyPool = new KeyPool(keyStorePath, STORE_PASS, keyId, keyPass);
            HAClient haClient = new HAClient(id, keyPool);
            haClient.interactive();
        } catch (KeyPoolException e) {
            System.err.println("Couldn't create keypool");
            System.exit(1);
        }


       /*
        try {
            // create Grid from the config file
            Grid grid = new Grid(id, gridConfigFile);
            User user = new User(id, grid, baseTime);
            user.start();
        } catch (UserException e) {
            e.printStackTrace();
        }*/
    }
}
