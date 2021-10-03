package pt.tecnico.hds.mad.lib.security;

import java.util.Set;
import java.util.HashSet;

public class NonceChecker {
    private Set<String> prevNonces;

    public NonceChecker() {
        this.prevNonces = new HashSet();
    }

    public boolean check(String nonce) {
        return this.prevNonces.add(nonce);
    }
}
