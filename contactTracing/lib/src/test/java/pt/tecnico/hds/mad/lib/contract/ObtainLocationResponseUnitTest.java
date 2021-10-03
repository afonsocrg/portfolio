package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidLocationProofResponseException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidObtainLocationResponseException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObtainLocationResponseUnitTest {

    @Test
    @DisplayName("Invalid Bad statuses with Reports")
    public void invalidStatusesWithReport() {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        UserLocationReport query = new UserLocationReport(new Report(record, Arrays.asList(proof)));

        Assertions.assertThrows(InvalidObtainLocationResponseException.class, () -> {
           new ObtainLocationResponse(ResponseStatus.INVALID_ID_REQUEST, query);
        });

        Assertions.assertThrows(InvalidObtainLocationResponseException.class, () -> {
            new ObtainLocationResponse(ResponseStatus.NO_INFORMATION_FOR_QUERY, query);
        });
    }

    @Test
    @DisplayName("OK Status without report")
    public void validStatusWithoutReport() {
        Assertions.assertThrows(InvalidObtainLocationResponseException.class, () -> {
            new ObtainLocationResponse(ResponseStatus.OK, null);
        });
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException, InvalidObtainLocationResponseException {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        UserLocationReport report = new UserLocationReport(new Report(record, Arrays.asList(proof)));
        ObtainLocationResponse request1 = new ObtainLocationResponse(ResponseStatus.OK, report);
        JsonObject json1 = request1.toJson();
        ObtainLocationResponse request2 = ObtainLocationResponse.fromJson(json1);

        Assertions.assertEquals(request1, request2);
        Assertions.assertEquals(request1.getUserLocationReport(), request2.getUserLocationReport());
    }

    @Test
    @DisplayName("Different messages different serializations")
    public void differentJson() throws InvalidJsonException, InvalidObtainLocationResponseException {
        Record record1 =  new Record("client1", 0, 1, 1);
        Proof proof1 = new Proof("client1", "INVALID BUT OK");

        Record record2 =  new Record("client2", 0, 2, 2);
        Proof proof2 = new Proof("client2", "INVALID BUT OK");
        UserLocationReport report1 = new UserLocationReport(new Report(record1, Arrays.asList(proof1)));
        UserLocationReport report2 = new UserLocationReport(new Report(record2, Arrays.asList(proof2)));
        ObtainLocationResponse request1 = new ObtainLocationResponse(ResponseStatus.OK, report1);
        ObtainLocationResponse request2 = new ObtainLocationResponse(ResponseStatus.OK, report2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(ObtainLocationResponse.fromJson(json1), request2);
        Assertions.assertNotEquals(ObtainLocationResponse.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() throws InvalidObtainLocationResponseException {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        ObtainLocationResponse request = new ObtainLocationResponse(ResponseStatus.OK, new UserLocationReport(new Report(record, Arrays.asList(proof))));
        JsonObject json = request.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() throws InvalidObtainLocationResponseException {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        ObtainLocationResponse request = new ObtainLocationResponse(ResponseStatus.OK, new UserLocationReport(new Report(record, Arrays.asList(proof))));
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofRequest.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (OK status without proof)")
    public void jsonMissingFields2() throws InvalidObtainLocationResponseException {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        ObtainLocationResponse m1 = new ObtainLocationResponse(ResponseStatus.OK, new UserLocationReport(new Report(record, Arrays.asList(proof))));
        JsonObject json = m1.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (bad statuses with proof)")
    public void jsonMissingFields3() throws InvalidObtainLocationResponseException {
        Record record =  new Record("client1", 0, 1, 1);
        Proof proof = new Proof("client1", "INVALID BUT OK");
        ObtainLocationResponse m1 = new ObtainLocationResponse(ResponseStatus.OK, new UserLocationReport(new Report(record, Arrays.asList(proof))));
        JsonObject json = m1.toJson();
        json.addProperty("status", ResponseStatus.INVALID_ID_REQUEST.getLabel());
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });

        json.addProperty("status", ResponseStatus.NO_INFORMATION_FOR_QUERY.getLabel());
        assertThrows(InvalidJsonException.class, () -> {
            LocationProofResponse.fromJson(json);
        });
    }
}
