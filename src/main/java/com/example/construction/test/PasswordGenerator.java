package com.example.construction.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "zakaria"; // Remplace par le mot de passe que tu veux hasher
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Mot de passe en clair : " + rawPassword);
        System.out.println("Mot de passe hashé   : " + encodedPassword);
    }
}