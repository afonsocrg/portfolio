package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersAtLocationReportUnitTest {

    private static Map<String, Proof> oneElementMap;
    private static Map<String, Proof> anotherOneElementMap;
    private static Map<String, Proof> twoElementMap;

    @BeforeAll
    public static void setUp() {
        Proof p1 = new Proof("client1", "INVALID BUT OK");
        Proof p2 = new Proof("client2", "INVALID BUT OK");
        oneElementMap = new HashMap<>();
        oneElementMap.put("client1", p1);
        anotherOneElementMap = new HashMap<>();
        anotherOneElementMap.put("client2", p2);
        twoElementMap = new HashMap<>();
        twoElementMap.put("client1", p1);
        twoElementMap.put("client2", p2);
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        UsersAtLocationReport report1 = new UsersAtLocationReport(oneElementMap);

        JsonObject json = report1.toJson();
        UsersAtLocationReport report2 = UsersAtLocationReport.fromJson(json);

        Assertions.assertEquals(report1, report2);
    }

    @Test
    @DisplayName("Different reports with different sizes, different serialization")
    public void differentSizesReport() throws InvalidJsonException {
        UsersAtLocationReport report1 = new UsersAtLocationReport(oneElementMap);
        UsersAtLocationReport report2 = new UsersAtLocationReport(twoElementMap);
        UsersAtLocationReport report3 = new UsersAtLocationReport(new HashMap<>());

        JsonObject json1 = report1.toJson();
        JsonObject json2 = report2.toJson();
        JsonObject json3 = report3.toJson();

        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json1.deepCopy()), report2);
        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json1), report3);
        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json2.deepCopy()), report1);
        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json2), report3);
        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json3.deepCopy()), report1);
        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json3), report2);
    }

    @Test
    @DisplayName("Different reports with same size different ids, different serialization")
    public void differentIdsReport() throws InvalidJsonException {
        UsersAtLocationReport report1 = new UsersAtLocationReport(oneElementMap);
        UsersAtLocationReport report2 = new UsersAtLocationReport(anotherOneElementMap);

        JsonObject json1 = report1.toJson();
        JsonObject json2 = report2.toJson();

        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json1), report2);
        Assertions.assertNotEquals(UsersAtLocationReport.fromJson(json2), report1);

    }

    @Test
    @DisplayName("Invalid Json (extra fields)")
    public void invalidJsonExtra() {
        UsersAtLocationReport report = new UsersAtLocationReport(oneElementMap);
        JsonObject json = report.toJson();
        json.addProperty("extra", "THIS IS AN INVALID EXTRA PROPERTY");
        Assertions.assertThrows(InvalidJsonException.class, () -> UsersAtLocationReport.fromJson(json));
    }

    @Test
    @DisplayName("Invalid Json (missing fields)")
    public void invalidJsonMissing() {
        UsersAtLocationReport report = new UsersAtLocationReport(oneElementMap);
        JsonObject json = report.toJson();
        json.remove("users");
        Assertions.assertThrows(InvalidJsonException.class, () -> UsersAtLocationReport.fromJson(json));
    }

}
