// src/main/java/com/example/construction/converter/RoleEnumConverter.java
package com.example.construction.converter;

import com.example.construction.entities.role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RoleEnumConverter implements Converter<String, role> {

    @Override
    public role convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            // Essayer de convertir directement
            return role.valueOf(source.trim().toLowerCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Gérer les variations
            String normalized = source.trim().toLowerCase()
                    .replace(" ", "_")
                    .replace("-", "_");

            for (role r : role.values()) {
                String roleName = r.name().toLowerCase();
                if (roleName.equals(normalized)) {
                    return r;
                }
            }

            throw new IllegalArgumentException("Rôle invalide: " + source +
                    ". Les valeurs possibles sont: " + java.util.Arrays.toString(role.values()));
        }
    }
}