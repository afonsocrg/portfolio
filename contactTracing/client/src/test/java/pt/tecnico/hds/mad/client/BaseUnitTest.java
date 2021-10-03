package pt.tecnico.hds.mad.client;

import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Properties;

public class BaseUnitTest {
    private static final String TEST_PROP_FILE = "/test.properties";
    protected static Properties testProps;

    @BeforeAll
    public static void oneTimeSetup() throws IOException {
        testProps = new Properties();
        testProps.load(BaseUnitTest.class.getResourceAsStream(TEST_PROP_FILE));
    }
}
