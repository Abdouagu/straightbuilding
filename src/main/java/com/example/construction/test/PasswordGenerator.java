package com.example.construction.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        // 1. Récupérer le mot de passe depuis les variables d'environnement
        String rawPassword = System.getenv("APP_PASSWORD");

        // 2. Vérifier que le mot de passe est fourni
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            System.err.println("ERREUR : Variable d'environnement APP_PASSWORD non définie");
            System.err.println("Usage : export APP_PASSWORD='ton_mot_de_passe'");
            System.exit(1);
        }

        // 3. Hasher le mot de passe
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);

        // 4. Afficher le résultat (sans le mot de passe en clair dans la sortie)
        System.out.println("Mot de passe hashé avec succès !");
        System.out.println("Hash BCrypt généré : " + encodedPassword);
        System.out.println("Note : Conservez ce hash pour votre base de données");
    }
}