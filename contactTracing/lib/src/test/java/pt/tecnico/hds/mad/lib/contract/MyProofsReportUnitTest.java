package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.ArrayList;
import java.util.List;

public class MyProofsReportUnitTest {

    private static List<RecordProofPair> oneElementList;
    private static List<RecordProofPair> anotherOneElementList;
    private static List<RecordProofPair> twoElementList;

    @BeforeAll
    public static void setUp() {
        oneElementList = new ArrayList<>();
        RecordProofPair pair1 = new RecordProofPair(new Record("client1", 1, 1, 1), new Proof("client1", "INVALID BUT OK"));
        oneElementList.add(pair1);
        anotherOneElementList = new ArrayList<>();
        RecordProofPair pair2 = new RecordProofPair(new Record("client2", 2, 2, 2), new Proof("client2", "INVALID BUT FINE"));
        anotherOneElementList.add(pair2);
        twoElementList = new ArrayList<>();
        twoElementList.add(pair1);
        twoElementList.add(pair2);
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        MyProofsReport report1 = new MyProofsReport(oneElementList);

        JsonObject json = report1.toJson();
        MyProofsReport report2 = MyProofsReport.fromJson(json);

        Assertions.assertEquals(report1, report2);
    }

    @Test
    @DisplayName("Different reports with different sizes, different serialization")
    public void differentSizesReport() throws InvalidJsonException {
        MyProofsReport report1 = new MyProofsReport(oneElementList);
        MyProofsReport report2 = new MyProofsReport(twoElementList);
        MyProofsReport report3 = new MyProofsReport(new ArrayList<>());

        JsonObject json1 = report1.toJson();
        JsonObject json2 = report2.toJson();
        JsonObject json3 = report3.toJson();

        Assertions.assertNotEquals(MyProofsReport.fromJson(json1.deepCopy()), report2);
        Assertions.assertNotEquals(MyProofsReport.fromJson(json1), report3);
        Assertions.assertNotEquals(MyProofsReport.fromJson(json2.deepCopy()), report1);
        Assertions.assertNotEquals(MyProofsReport.fromJson(json2), report3);
        Assertions.assertNotEquals(MyProofsReport.fromJson(json3.deepCopy()), report1);
        Assertions.assertNotEquals(MyProofsReport.fromJson(json3), report2);
    }

    @Test
    @DisplayName("Different reports with same size different pairs, different serialization")
    public void differentIdsReport() throws InvalidJsonException {
        MyProofsReport report1 = new MyProofsReport(oneElementList);
        MyProofsReport report2 = new MyProofsReport(anotherOneElementList);

        JsonObject json1 = report1.toJson();
        JsonObject json2 = report2.toJson();

        Assertions.assertNotEquals(MyProofsReport.fromJson(json1), report2);
        Assertions.assertNotEquals(MyProofsReport.fromJson(json2), report1);

    }

    @Test
    @DisplayName("Invalid Json (extra fields)")
    public void invalidJsonExtra() {
        MyProofsReport report = new MyProofsReport(oneElementList);
        JsonObject json = report.toJson();
        json.addProperty("extra", "THIS IS AN INVALID EXTRA PROPERTY");
        Assertions.assertThrows(InvalidJsonException.class, () -> MyProofsReport.fromJson(json));
    }

    @Test
    @DisplayName("Invalid Json (missing fields)")
    public void invalidJsonMissing() {
        MyProofsReport report = new MyProofsReport(oneElementList);
        JsonObject json = report.toJson();
        json.remove("proofs");
        Assertions.assertThrows(InvalidJsonException.class, () -> MyProofsReport.fromJson(json));
    }

}
