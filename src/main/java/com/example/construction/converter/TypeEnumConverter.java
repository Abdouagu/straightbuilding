// src/main/java/com/example/construction/converter/TypeEnumConverter.java
package com.example.construction.converter;

import com.example.construction.entities.Type;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TypeEnumConverter implements Converter<String, Type> {

    @Override
    public Type convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            // Essayer de convertir directement
            return Type.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Gérer les variations de casse et les accents
            String normalized = source.trim().toLowerCase()
                    .replace("ç", "c")
                    .replace("é", "e")
                    .replace("è", "e")
                    .replace("ê", "e")
                    .replace(" ", "_");

            // Gestion spéciale pour les noms français
            if ("macon".equals(normalized) || "maçon".equals(normalized)) {
                return Type.macon;
            }
            if ("greutier".equals(normalized) || "gréutier".equals(normalized)) {
                return Type.greutier;
            }

            for (Type type : Type.values()) {
                String typeName = type.name().toLowerCase();
                if (typeName.equals(normalized)) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Type invalide: " + source +
                    ". Les valeurs possibles sont: " + java.util.Arrays.toString(Type.values()));
        }
    }
}