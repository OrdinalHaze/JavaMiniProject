package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    // SHA-256 hashing (hex)
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }
}


