package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

import java.util.ArrayList;
import java.util.Arrays;

public class MyProofsQueryUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        MyProofsQuery query1 = new MyProofsQuery("client1", Arrays.asList(1, 2));

        JsonObject json = query1.toJson();
        MyProofsQuery query2 = MyProofsQuery.fromJson(json);

        Assertions.assertEquals(query1, query2);
    }

    @Test
    @DisplayName("Different queries, different serialization")
    public void differentQueries() throws InvalidJsonException {
        MyProofsQuery query1 = new MyProofsQuery("client1", Arrays.asList(1, 2));
        MyProofsQuery query2 = new MyProofsQuery("client2", Arrays.asList(1, 2));
        MyProofsQuery query3 = new MyProofsQuery("client1", Arrays.asList(1));
        MyProofsQuery query4 = new MyProofsQuery("client2", Arrays.asList(1));

        JsonObject json1 = query1.toJson();
        JsonObject json2 = query2.toJson();
        JsonObject json3 = query3.toJson();
        JsonObject json4 = query4.toJson();

        Assertions.assertNotEquals(MyProofsQuery.fromJson(json2), query1);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json3), query1);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json4), query1);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json1), query2);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json3), query2);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json4), query2);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json1), query3);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json2), query3);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json4), query3);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json1), query4);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json2), query4);
        Assertions.assertNotEquals(MyProofsQuery.fromJson(json3), query4);
    }

    @Test
    @DisplayName("Invalid Json (extra fields)")
    public void invalidJsonExtra() {
        MyProofsQuery query = new MyProofsQuery("client1", Arrays.asList(1, 2));
        JsonObject json = query.toJson();
        json.addProperty("extra", "THIS IS AN INVALID EXTRA PROPERTY");

        Assertions.assertThrows(InvalidJsonException.class, () -> { MyProofsQuery.fromJson(json); });
    }

    @Test
    @DisplayName("Invalid Json (missing fields)")
    public void invalidJsonMissing() {
        MyProofsQuery query = new MyProofsQuery("client1", Arrays.asList(1, 2));
        JsonObject json = query.toJson();
        JsonObject tmp = json.deepCopy();

        tmp.remove("user_id");
        JsonObject finalTmp = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { MyProofsQuery.fromJson(finalTmp); });

        tmp = json.deepCopy();
        tmp.remove("epochs");
        JsonObject finalTmp1 = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { MyProofsQuery.fromJson(finalTmp1); });
    }

}
