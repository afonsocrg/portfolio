package pt.tecnico.hds.mad.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.tecnico.hds.mad.client.exceptions.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// TODO: This is very static, come up with a better way to test this
public class GridUnitTest extends BaseUnitTest {
    private static Grid grid;
    private static String myId = "client1";
    private static int testEpoch = 0;

    @BeforeAll
    public static void setUp() {
        try {
            grid = new Grid(testProps.getProperty("grid.config"));
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test My position")
    public void testMyPos() throws InvalidEpochException, NoSuchUserException {
        assertEquals(new Position(1, 1), grid.getUserPosition(testEpoch, myId));
    }

    @Test
    @DisplayName("Test isNear")
    public void testIsNear() throws InvalidEpochException, NoSuchUserException {
        // With the current distance user 1 is near user 2 and 4 but user 3 is not
        assertTrue(grid.isNearAtEpoch(testEpoch, myId, "client2"));
        assertFalse(grid.isNearAtEpoch(testEpoch, myId, "client3"));
        assertTrue(grid.isNearAtEpoch(testEpoch, myId, "client4"));
    }

    @Test
    @DisplayName("Test whoIsNear")
    public void testWhoIsNear() throws InvalidEpochException, NoSuchUserException {
        // User 2 is at distance 2 and user 4 is in the same position
        List<GridUser> list = new ArrayList<>();
        list.add(grid.getUser(testEpoch, "client4"));
        list.add(grid.getUser(testEpoch, "client2"));
        assertEquals(list, grid.whoIsNearAtEpoch(testEpoch, myId));
    }
}
