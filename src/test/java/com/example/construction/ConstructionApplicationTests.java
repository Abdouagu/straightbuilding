package com.example.construction;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// ENLÈVE @Import(TestcontainersConfiguration.class) si présent
@ActiveProfiles("test")  // ← IMPORTANT : utilise le profil "test"
@SpringBootTest
class ConstructionApplicationTests {

    @Test
    void contextLoads() {
        // Test simple
    }
}