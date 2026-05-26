package org.example;

import org.mindrot.jbcrypt.BCrypt;

public class Hasher {
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) return false;
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
