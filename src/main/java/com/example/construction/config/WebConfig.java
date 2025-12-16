// src/main/java/com/example/construction/config/WebConfig.java
package com.example.construction.config;

import com.example.construction.converter.EtatEnumConverter;
import com.example.construction.converter.RoleEnumConverter;
import com.example.construction.converter.TypeEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final EtatEnumConverter etatEnumConverter;
    private final RoleEnumConverter roleEnumConverter;
    private final TypeEnumConverter typeEnumConverter;

    public WebConfig(EtatEnumConverter etatEnumConverter,
                     RoleEnumConverter roleEnumConverter,
                     TypeEnumConverter typeEnumConverter) {
        this.etatEnumConverter = etatEnumConverter;
        this.roleEnumConverter = roleEnumConverter;
        this.typeEnumConverter = typeEnumConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(etatEnumConverter);
        registry.addConverter(roleEnumConverter);
        registry.addConverter(typeEnumConverter);
    }
}