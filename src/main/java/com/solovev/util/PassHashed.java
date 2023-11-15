package com.solovev.util;

import com.password4j.Hash;
import com.password4j.Password;

/**
 * Class used to hash passwords
 */
public class PassHashed {
    private static final String PEPPER = "pepper";
    public static String hash(String pass){
        return Password.hash(pass).addPepper(PEPPER).addSalt(pass).withScrypt().getResult();
    }
}
