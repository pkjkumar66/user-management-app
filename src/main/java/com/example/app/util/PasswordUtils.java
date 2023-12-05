package com.example.app.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordUtils {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordUtils(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String generateSalt() {
        return Base64.getEncoder().encodeToString(new SecureRandom().generateSeed(16));
    }

    public String hashPassword(String password, String salt) {
        return passwordEncoder.encode(password + salt);
    }

    public boolean isPasswordValid(String rawPassword, String encodedPassword, String salt) {
        String hashedPassword = hashPassword(rawPassword, salt);
        return passwordEncoder.matches(encodedPassword, hashedPassword);
    }
}
