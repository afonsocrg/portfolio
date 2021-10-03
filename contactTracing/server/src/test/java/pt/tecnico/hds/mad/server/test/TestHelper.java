package pt.tecnico.hds.mad.server.test;

import java.io.File;

public class TestHelper {
    public static void deleteDatabase(String path) {
        (new File(path)).delete();
    }
}
