// src/main/java/com/example/construction/converter/EtatEnumConverter.java
package com.example.construction.converter;

import com.example.construction.entities.Etat;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EtatEnumConverter implements Converter<String, Etat> {

    @Override
    public Etat convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            // Essayer de convertir directement
            return Etat.valueOf(source.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Gérer les variations
            String normalized = source.trim().toUpperCase()
                    .replace(" ", "_")
                    .replace("É", "E")
                    .replace("È", "E")
                    .replace("Ê", "E");

            for (Etat etat : Etat.values()) {
                String etatName = etat.name().toUpperCase();
                if (etatName.equals(normalized)) {
                    return etat;
                }
            }

            throw new IllegalArgumentException("État invalide: " + source +
                    ". Les valeurs possibles sont: " + java.util.Arrays.toString(Etat.values()));
        }
    }
}