package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class UserLocationQueryUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        UserLocationQuery query1 = new UserLocationQuery("client1", 1);

        JsonObject json = query1.toJson();
        UserLocationQuery query2 = UserLocationQuery.fromJson(json);

        Assertions.assertEquals(query1, query2);
    }

    @Test
    @DisplayName("Different queries, different serialization")
    public void differentQueries() throws InvalidJsonException {
        UserLocationQuery query1 = new UserLocationQuery("client1", 1);
        UserLocationQuery query2 = new UserLocationQuery("client2", 2);

        JsonObject json1 = query1.toJson();
        JsonObject json2 = query2.toJson();

        Assertions.assertNotEquals(UserLocationQuery.fromJson(json2), query1);
        Assertions.assertNotEquals(UserLocationQuery.fromJson(json1), query2);
    }

    @Test
    @DisplayName("Invalid Json (extra fields)")
    public void invalidJsonExtra() {
        UserLocationQuery query = new UserLocationQuery("client1", 1);
        JsonObject json = query.toJson();
        json.addProperty("extra", "THIS IS AN INVALID EXTRA PROPERTY");

        Assertions.assertThrows(InvalidJsonException.class, () -> { UserLocationQuery.fromJson(json); });
    }

    @Test
    @DisplayName("Invalid Json (missing fields)")
    public void invalidJsonMissing() {
        UserLocationQuery query = new UserLocationQuery("client1", 1);
        JsonObject json = query.toJson();
        JsonObject tmp = json.deepCopy();

        tmp.remove("user_id");
        JsonObject finalTmp = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { UserLocationQuery.fromJson(finalTmp); });

        tmp = json.deepCopy();
        tmp.remove("epoch");
        JsonObject finalTmp1 = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { UserLocationQuery.fromJson(finalTmp1); });
    }

}
