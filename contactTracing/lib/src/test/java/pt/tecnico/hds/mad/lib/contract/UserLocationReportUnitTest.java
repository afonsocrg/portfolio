package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.Arrays;
import java.util.Collections;

public class UserLocationReportUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        UserLocationReport report1 = new UserLocationReport(new Report(record, Arrays.asList(proof)));

        JsonObject json = report1.toJson();
        UserLocationReport report2 = UserLocationReport.fromJson(json);

        Assertions.assertEquals(report1, report2);
    }

    @Test
    @DisplayName("Different reports, different serialization")
    public void differentReport() throws InvalidJsonException {
        Record record1 =  new Record("client1", 0, 1, 1);
        Record record2 = new Record("client2", 0, 2, 2);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        UserLocationReport report1 = new UserLocationReport(new Report(record1, Arrays.asList(proof)));
        UserLocationReport report2 = new UserLocationReport(new Report(record2, Arrays.asList(proof)));

        JsonObject json1 = report1.toJson();
        JsonObject json2 = report2.toJson();

        Assertions.assertNotEquals(UserLocationReport.fromJson(json2), report1);
        Assertions.assertNotEquals(UserLocationReport.fromJson(json1), report2);
    }

    @Test
    @DisplayName("Invalid Json (extra fields)")
    public void invalidJsonExtra() {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        UserLocationReport report = new UserLocationReport(new Report(record, Arrays.asList(proof)));
        JsonObject json = report.toJson();
        json.addProperty("extra", "THIS IS AN INVALID EXTRA PROPERTY");

        Assertions.assertThrows(InvalidJsonException.class, () -> { UserLocationReport.fromJson(json); });
    }

    @Test
    @DisplayName("Invalid Json (missing fields)")
    public void invalidJsonMissing() {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        UserLocationReport report = new UserLocationReport(new Report(record, Arrays.asList(proof)));
        JsonObject json = report.toJson();
        json.remove("report");
        Assertions.assertThrows(InvalidJsonException.class, () -> { UserLocationReport.fromJson(json); });
    }

}
