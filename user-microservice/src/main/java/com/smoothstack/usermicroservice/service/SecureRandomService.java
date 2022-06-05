package com.smoothstack.usermicroservice.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class SecureRandomService {

    private static final String ALLOWED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private SecureRandom secureRandom;

    public SecureRandomService() {
        secureRandom = new SecureRandom();
    }

    // Generates a random web-safe string useful for confirmation codes or generated secrets.
    public String generateAlphanumericString(int length) {
         return secureRandom
            .ints(length, 0, ALLOWED_CHARS.length())
            .mapToObj(i -> ALLOWED_CHARS.charAt(i))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}
