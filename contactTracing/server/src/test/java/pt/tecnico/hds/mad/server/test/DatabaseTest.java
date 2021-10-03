package pt.tecnico.hds.mad.server.test;

import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.contract.Record;
import pt.tecnico.hds.mad.server.Database;
import pt.tecnico.hds.mad.server.exceptions.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DatabaseTest {
    static Database db;
    static String DB_PATH = "test.db";
    @BeforeEach
    void setup() throws DatabaseException {
        TestHelper.deleteDatabase(DB_PATH);
        db = new Database(DB_PATH);
    }

    @Test
    @DisplayName("Data is saved")
    void testSavedData() throws DatabaseException {
        Record r = new Record("client1", 2, 3, 4);
        db.setRecord(r);

        List<Record> l = db.getRecords();
        assertEquals(1, l.size());
        assertEquals(r, l.get(0));
    }

    @Test
    @DisplayName("More than one record saved")
    void testMoreRecordsSaved() throws DatabaseException {
        db.setRecord(new Record("client1", 2, 3, 4));
        db.setRecord(new Record("client2", 2, 3, 4));
        db.setRecord(new Record("client3", 2, 3, 4));

        List<Record> l = db.getRecords();
        assertEquals(3, l.size());
    }

    @AfterEach
    void tearDown() throws DatabaseException {
        TestHelper.deleteDatabase(DB_PATH);
    }

}
