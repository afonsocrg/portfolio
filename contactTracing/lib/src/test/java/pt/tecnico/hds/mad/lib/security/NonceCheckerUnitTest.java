package pt.tecnico.hds.mad.lib.security;

import org.junit.jupiter.api.*;
import pt.tecnico.hds.mad.lib.security.NonceChecker;

import static org.junit.jupiter.api.Assertions.*;

public class NonceCheckerUnitTest {

    @Test
    @DisplayName("Test no duplicate nonces")
    public void noDuplicateTest() {
        NonceChecker checker = new NonceChecker();
        assertTrue(checker.check("string1"));
        assertTrue(checker.check("string2"));
        assertTrue(checker.check("string3"));
        assertTrue(checker.check("string4"));

        assertFalse(checker.check("string1"));
        assertFalse(checker.check("string2"));
        assertFalse(checker.check("string3"));
        assertFalse(checker.check("string4"));
    }
}

