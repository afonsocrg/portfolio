package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;
import pt.tecnico.hds.mad.lib.exceptions.InvalidObtainLocationResponseException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyProofsResponseUnitTest {

    private static MyProofsReport report1;
    private static MyProofsReport report2;
    private static MyProofsReport report3;

    @BeforeAll
    public static void setUp() {
        ArrayList<RecordProofPair> list1 = new ArrayList<>();
        RecordProofPair pair1 = new RecordProofPair(new Record("client1", 1, 1, 1), new Proof("client1", "INVALID BUT OK"));
        list1.add(pair1);
        report1 = new MyProofsReport(list1);

        ArrayList<RecordProofPair> list2 = new ArrayList<>();
        RecordProofPair pair2 = new RecordProofPair(new Record("client2", 1, 2, 2), new Proof("client2", "INVALID BUT OK"));
        list2.add(pair2);
        report2 = new MyProofsReport(list2);

        ArrayList<RecordProofPair> list3 = new ArrayList<>();
        list3.add(pair1);
        list3.add(pair2);
        report3 = new MyProofsReport(list3);
    }

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException, InvalidObtainLocationResponseException {
        MyProofsResponse request1 = new MyProofsResponse(ResponseStatus.OK, report1);
        JsonObject json1 = request1.toJson();
        MyProofsResponse request2 = MyProofsResponse.fromJson(json1);

        Assertions.assertEquals(request1, request2);
        Assertions.assertEquals(request1.getMyProofsReport(), request2.getMyProofsReport());
    }

    @Test
    @DisplayName("Different messages different sizes different serializations")
    public void differentSizesJson() throws InvalidJsonException {
        MyProofsResponse response1 = new MyProofsResponse(ResponseStatus.OK, report1);
        MyProofsResponse response2 = new MyProofsResponse(ResponseStatus.OK, report3);
        JsonObject json1 = response1.toJson();
        JsonObject json2 = response2.toJson();

        Assertions.assertNotEquals(MyProofsResponse.fromJson(json1), response2);
        Assertions.assertNotEquals(MyProofsResponse.fromJson(json2), response1);
    }

    @Test
    @DisplayName("Different messages different pairs different serializations")
    public void differentIdsJson() throws InvalidJsonException{
        MyProofsResponse request1 = new MyProofsResponse(ResponseStatus.OK, report1);
        MyProofsResponse request2 = new MyProofsResponse(ResponseStatus.OK, report2);
        JsonObject json1 = request1.toJson();
        JsonObject json2 = request2.toJson();

        Assertions.assertNotEquals(MyProofsResponse.fromJson(json1), request2);
        Assertions.assertNotEquals(MyProofsResponse.fromJson(json2), request1);
    }

    @Test
    @DisplayName("Invalid Json format (extra fields)")
    public void jsonExtraFields() throws InvalidObtainLocationResponseException {
        MyProofsResponse request = new MyProofsResponse(ResponseStatus.OK, report1);
        JsonObject json = request.toJson();
        json.addProperty("extra", "THIS IS INVALID");
        assertThrows(InvalidJsonException.class, () -> {
            MyProofsResponse.fromJson(json);
        });
    }

    @Test
    @DisplayName("Invalid Json format (missing fields)")
    public void jsonMissingFields() throws InvalidObtainLocationResponseException {
        MyProofsResponse request = new MyProofsResponse(ResponseStatus.OK, report1);
        JsonObject json = request.toJson();
        json.remove("content");
        assertThrows(InvalidJsonException.class, () -> {
            MyProofsResponse.fromJson(json);
        });
    }
}
