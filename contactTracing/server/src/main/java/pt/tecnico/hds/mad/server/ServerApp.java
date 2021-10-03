package pt.tecnico.hds.mad.server;

import pt.tecnico.hds.mad.lib.security.KeyPool;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;

import pt.tecnico.hds.mad.server.exceptions.DatabaseCreationException;

public class ServerApp {

    private static final String DB_PATH_SUFFIX = ".db";
    private static final String STORE_PATH = "../global.keystore.jks";
    private static final String STORE_PASS = "globalpass";
    private static final String KEY_PASS_SUFFIX = "pass";

    public static void main(String[] args) {
        String keyStorePath = STORE_PATH;

        if (args.length < 1) {
            System.out.println("Missing arguments");
            System.out.println("Usage java " + ServerApp.class.getName() + " <id> [-d <database_file>] [-k <keystore>]");
        }

        String myId = "server" + args[0]; // To keep the calling arguments the same, can change if we want

        String databasePath = "data_"+myId+DB_PATH_SUFFIX;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == "-d" && i+1 < args.length) {
                databasePath = args[++i];
            } else if (args[i] == "-k" && i+1 < args.length) {
                keyStorePath = args[++i];
            }
        }

        try {
            Database db = new Database(databasePath);
            KeyPool keyPool = new KeyPool(keyStorePath, STORE_PASS, myId, myId + KEY_PASS_SUFFIX);
            (new Server(myId, db, keyPool)).run();
        } catch (DatabaseCreationException e) {
            System.err.println("Couldn't create database");
            System.exit(1);
        } catch (KeyPoolException e) {
            System.err.println("Couldn't create keypool");
            System.exit(1);
        }
    }
}
