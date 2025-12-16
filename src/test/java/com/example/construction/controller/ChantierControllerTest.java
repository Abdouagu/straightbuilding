// src/test/java/com/example/construction/controller/ChantierControllerTest.java
package com.example.construction.controller;

import com.example.construction.dto.ChantierDTO;
import com.example.construction.entities.Etat;
import com.example.construction.service.ChantierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChantierController.class)
@Import(ChantierController.class) // Nécessaire pour injecter les dépendances
class ChantierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Nouvelle annotation au lieu de @MockBean
    private ChantierService chantierService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChantierDTO chantierDTO;

    @BeforeEach
    void setUp() {
        chantierDTO = new ChantierDTO();
        chantierDTO.setId(1);
        chantierDTO.setNomProjet("Projet Test");
        chantierDTO.setTitreFoncier("TF123");
        chantierDTO.setNumPolice(12345);
        chantierDTO.setDateOuverture(new Date());
        chantierDTO.setDateContrat(new Date());
        chantierDTO.setAdresse("123 Rue Test");
        chantierDTO.setDuree(180);
        chantierDTO.setEtat(Etat.EN_COURS);
        chantierDTO.setBudget(100000.0f);
        chantierDTO.setId_client(1);
    }

    @Test
    void testGetChantierById_Success() throws Exception {
        // Given
        when(chantierService.getChantierById(1)).thenReturn(chantierDTO);

        // When & Then
        mockMvc.perform(get("/api/chantiers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomProjet").value("Projet Test"));
    }

    @Test
    void testCreateChantier_Success() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(12345)).thenReturn(false);
        when(chantierService.existsByTitreFoncier("TF123")).thenReturn(false);
        when(chantierService.existsByNomProjet("Projet Test")).thenReturn(false);
        when(chantierService.saveChantier(any(ChantierDTO.class))).thenReturn(chantierDTO);

        // When & Then
        mockMvc.perform(post("/api/chantiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ... autres tests ...
}