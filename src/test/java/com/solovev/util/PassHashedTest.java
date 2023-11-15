package com.solovev.util;

import com.password4j.Hash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PassHashedTest {
    @Test
    public void sameHash() {
        assertEquals(pass1Hash, PassHashed.hash(pass1));
    }

    @Test
    public void differentHash() {
        assertNotEquals(pass1Hash, PassHashed.hash(pass2));
    }

    private final String pass1 = "pass1";
    private final String pass1Hash = PassHashed.hash(pass1);
    private final String pass2 = "pass2";

}