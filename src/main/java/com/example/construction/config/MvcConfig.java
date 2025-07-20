package com.example.construction.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les fichiers CSS et images depuis templates pour la page login
        registry.addResourceHandler("/style.css")
                .addResourceLocations("classpath:/templates/");

        registry.addResourceHandler("/straight-building-logo.png")
                .addResourceLocations("classpath:/templates/");

        // Servir les fichiers depuis static/AdminBSBMaterialDesign-master
        registry.addResourceHandler("/AdminBSBMaterialDesign-master/**")
                .addResourceLocations("classpath:/static/AdminBSBMaterialDesign-master/");

        // Servir les autres fichiers statiques
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry, String logicalPath) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Le double slash à la fin est important
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }

        registry.addResourceHandler(logicalPath + "**")
                .addResourceLocations("file:" + uploadPath);

        System.out.println("Chemin configuré: " + logicalPath + "** -> file:" + uploadPath);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:63342") // Autorise les requêtes depuis IntelliJ IDEA
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }


}
