package pt.tecnico.hds.mad.lib.contract;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.lib.exceptions.InvalidJsonException;

public class UsersAtLocationQueryUnitTest {

    @Test
    @DisplayName("Correct de/serialization")
    public void correctDeSerialization() throws InvalidJsonException {
        UsersAtLocationQuery query1 = new UsersAtLocationQuery(1, 1, 1);

        JsonObject json = query1.toJson();
        UsersAtLocationQuery query2 = UsersAtLocationQuery.fromJson(json);

        Assertions.assertEquals(query1, query2);
    }

    @Test
    @DisplayName("Different queries, different serialization")
    public void differentQueries() throws InvalidJsonException {
        UsersAtLocationQuery query1 = new UsersAtLocationQuery(1, 1, 1);
        UsersAtLocationQuery query2 = new UsersAtLocationQuery(2, 2, 2);

        JsonObject json1 = query1.toJson();
        JsonObject json2 = query2.toJson();

        Assertions.assertNotEquals(UsersAtLocationQuery.fromJson(json2), query1);
        Assertions.assertNotEquals(UsersAtLocationQuery.fromJson(json1), query2);
    }

    @Test
    @DisplayName("Invalid Json (extra fields)")
    public void invalidJsonExtra() {
        UsersAtLocationQuery query = new UsersAtLocationQuery(1, 1, 1);
        JsonObject json = query.toJson();
        json.addProperty("extra", "THIS IS AN INVALID EXTRA PROPERTY");

        Assertions.assertThrows(InvalidJsonException.class, () -> { UsersAtLocationQuery.fromJson(json); });
    }

    @Test
    @DisplayName("Invalid Json (missing fields)")
    public void invalidJsonMissing() {
        UsersAtLocationQuery query = new UsersAtLocationQuery(1, 1, 1);
        JsonObject json = query.toJson();
        JsonObject tmp = json.deepCopy();

        tmp.remove("x");
        JsonObject finalTmp = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { UsersAtLocationQuery.fromJson(finalTmp); });

        tmp = json.deepCopy();
        tmp.remove("y");
        JsonObject finalTmp1 = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { UsersAtLocationQuery.fromJson(finalTmp1); });

        tmp = json.deepCopy();
        tmp.remove("epoch");
        JsonObject finalTmp2 = tmp;
        Assertions.assertThrows(InvalidJsonException.class, () -> { UsersAtLocationQuery.fromJson(finalTmp2); });
    }

}
