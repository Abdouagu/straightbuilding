package com.example.construction.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final String CIN_DIRECTORY = "C:/MesPhotos/cin/";
    private final String CNSS_DIRECTORY = "C:/MesPhotos/cnss/";

    @GetMapping("/cin/{fileName}")
    public ResponseEntity<Resource> getCINImage(@PathVariable String fileName) {
        return getImageAsResource(CIN_DIRECTORY + fileName);
    }

    @GetMapping("/cnss/{fileName}")
    public ResponseEntity<Resource> getCNSSImage(@PathVariable String fileName) {
        return getImageAsResource(CNSS_DIRECTORY + fileName);
    }

    private ResponseEntity<Resource> getImageAsResource(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(filePath);
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
