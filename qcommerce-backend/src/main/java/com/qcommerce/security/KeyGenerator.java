package com.qcommerce.security;
import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // Generate a cryptographically strong random number suitable for HMAC-SHA256
        // A key of 256 bits (32 bytes) is recommended for HS256
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(keyBytes);

        // Encode the key into a Base64 string for easy storage and use in properties files
        String encodedKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated JWT Secret Key: " + encodedKey);
    }
}