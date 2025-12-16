package com.example.construction.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Générateur de hash BCrypt pour les mots de passe.
 * Utilise une variable d'environnement comme source.
 */
public class PasswordGenerator {

    private static final String ENV_VAR_NAME = "APP_SECRET_KEY";
    private static final String ERROR_MSG =
            "Configuration manquante : la variable " + ENV_VAR_NAME + " n'est pas définie";
    private static final String USAGE_MSG =
            "Définir la variable : export " + ENV_VAR_NAME + "=\"votre_secret\"";

    public static void main(String[] args) {
        try {
            String input = getInputFromEnv();
            String hashed = hashPassword(input);
            displayResult(hashed);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println(USAGE_MSG);
            System.exit(1);
        }
    }

    private static String getInputFromEnv() {
        String value = System.getenv(ENV_VAR_NAME);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_MSG);
        }
        return value.trim();
    }

    private static String hashPassword(String plainText) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainText);
    }

    private static void displayResult(String hashedPassword) {
        System.out.println("✅ Hash généré avec succès");
        System.out.println("🔐 Résultat BCrypt :");
        System.out.println(hashedPassword);
        System.out.println("\n⚠️  Conservez ce hash en lieu sûr !");
    }
}