package com.dvm.bookstore.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();

    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
