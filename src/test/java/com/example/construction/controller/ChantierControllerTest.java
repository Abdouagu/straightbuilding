// src/test/java/com/example/construction/controller/ChantierControllerTest.java
package com.example.construction.controller;

import com.example.construction.dto.ChantierDTO;
import com.example.construction.entities.Etat;
import com.example.construction.service.ChantierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChantierController.class)
@Import(ChantierController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChantierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChantierService chantierService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChantierDTO chantierDTO;
    private ChantierDTO chantierDTO2;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        chantierDTO = new ChantierDTO();
        chantierDTO.setId(1);
        chantierDTO.setNomProjet("Projet Test");
        chantierDTO.setTitreFoncier("TF123");
        chantierDTO.setNumPolice(12345);
        chantierDTO.setDateOuverture(sdf.parse("2024-01-15"));
        chantierDTO.setDateContrat(sdf.parse("2024-01-10"));
        chantierDTO.setAdresse("123 Rue Test");
        chantierDTO.setDuree(180);
        chantierDTO.setEtat(Etat.EN_COURS);
        chantierDTO.setBudget(100000.0f);
        chantierDTO.setId_client(1);

        chantierDTO2 = new ChantierDTO();
        chantierDTO2.setId(2);
        chantierDTO2.setNomProjet("Projet Test 2");
        chantierDTO2.setTitreFoncier("TF456");
        chantierDTO2.setNumPolice(67890);
        chantierDTO2.setDateOuverture(sdf.parse("2024-02-01"));
        chantierDTO2.setDateContrat(sdf.parse("2024-01-25"));
        chantierDTO2.setAdresse("456 Autre Rue");
        chantierDTO2.setDuree(200);
        chantierDTO2.setEtat(Etat.TERMINE);
        chantierDTO2.setBudget(150000.0f);
        chantierDTO2.setId_client(2);
    }

    @Test
    void testGetChantierById_Success() throws Exception {
        // Given
        when(chantierService.getChantierById(1)).thenReturn(chantierDTO);

        // When & Then
        mockMvc.perform(get("/api/chantiers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomProjet").value("Projet Test"))
                .andExpect(jsonPath("$.numPolice").value(12345));
    }

    @Test
    void testGetChantierById_NotFound() throws Exception {
        // Given
        when(chantierService.getChantierById(99))
                .thenThrow(new RuntimeException("Chantier non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/chantiers/99"))
                .andExpect(status().isNotFound());
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomProjet").value("Projet Test"));
    }

    @Test
    void testCreateChantier_ConflictNumPolice() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(12345)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/chantiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateChantier_ConflictTitreFoncier() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(12345)).thenReturn(false);
        when(chantierService.existsByTitreFoncier("TF123")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/chantiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateChantier_ConflictNomProjet() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(12345)).thenReturn(false);
        when(chantierService.existsByTitreFoncier("TF123")).thenReturn(false);
        when(chantierService.existsByNomProjet("Projet Test")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/chantiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAllChantiers_Success() throws Exception {
        // Given
        List<ChantierDTO> chantiers = Arrays.asList(chantierDTO, chantierDTO2);
        when(chantierService.getAllChantiers()).thenReturn(chantiers);

        // When & Then
        mockMvc.perform(get("/api/chantiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nomProjet").value("Projet Test"))
                .andExpect(jsonPath("$[1].nomProjet").value("Projet Test 2"));
    }

    @Test
    void testUpdateChantier_Success() throws Exception {
        // Given
        ChantierDTO existingChantier = new ChantierDTO();
        existingChantier.setId(1);
        existingChantier.setNumPolice(11111);
        existingChantier.setTitreFoncier("OLD_TF");
        existingChantier.setNomProjet("Old Projet");

        ChantierDTO updatedChantier = new ChantierDTO();
        updatedChantier.setId(1);
        updatedChantier.setNomProjet("Projet Modifié");
        updatedChantier.setNumPolice(12345);
        updatedChantier.setTitreFoncier("TF123");

        when(chantierService.getChantierById(1)).thenReturn(existingChantier);
        when(chantierService.existsByNumPoliceAndIdNot(12345, 1)).thenReturn(false);
        when(chantierService.existsByTitreFoncierAndIdNot("TF123", 1)).thenReturn(false);
        when(chantierService.existsByNomProjetAndIdNot("Projet Modifié", 1)).thenReturn(false);
        when(chantierService.updateChantier(eq(1), any(ChantierDTO.class))).thenReturn(updatedChantier);

        // When & Then
        mockMvc.perform(put("/api/chantiers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedChantier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomProjet").value("Projet Modifié"));
    }

    @Test
    void testUpdateChantier_ConflictNumPolice() throws Exception {
        // Given
        ChantierDTO existingChantier = new ChantierDTO();
        existingChantier.setId(1);
        existingChantier.setNumPolice(11111);

        when(chantierService.getChantierById(1)).thenReturn(existingChantier);
        when(chantierService.existsByNumPoliceAndIdNot(12345, 1)).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/chantiers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateChantier_NotFound() throws Exception {
        // Given
        when(chantierService.getChantierById(99))
                .thenThrow(new RuntimeException("Chantier non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/chantiers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteChantier_Success() throws Exception {
        // Given
        doNothing().when(chantierService).deleteChantier(1);

        // When & Then
        mockMvc.perform(delete("/api/chantiers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteChantier_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Chantier non trouvé"))
                .when(chantierService).deleteChantier(99);

        // When & Then
        mockMvc.perform(delete("/api/chantiers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetChantierByNomProjet_Success() throws Exception {
        // Given
        when(chantierService.findByNomProjet("Projet Test")).thenReturn(chantierDTO);

        // When & Then
        mockMvc.perform(get("/api/chantiers/by-nom-projet/Projet Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomProjet").value("Projet Test"));
    }

    @Test
    void testGetChantierByTitreFoncier_Success() throws Exception {
        // Given
        when(chantierService.findByTitreFoncier("TF123")).thenReturn(chantierDTO);

        // When & Then
        mockMvc.perform(get("/api/chantiers/by-titre-foncier/TF123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titreFoncier").value("TF123"));
    }

    @Test
    void testGetChantierByNumPolice_Success() throws Exception {
        // Given
        when(chantierService.findByNumPolice(12345)).thenReturn(chantierDTO);

        // When & Then
        mockMvc.perform(get("/api/chantiers/by-num-police/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPolice").value(12345));
    }

    @Test
    void testGetChantiersByEtat_Success() throws Exception {
        // Given
        List<ChantierDTO> chantiers = Arrays.asList(chantierDTO);
        when(chantierService.findByEtat(Etat.EN_COURS)).thenReturn(chantiers);

        // When & Then
        mockMvc.perform(get("/api/chantiers/by-etat/EN_COURS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].etat").value("EN_COURS"));
    }

    @Test
    void testGetChantiersByClientId_Success() throws Exception {
        // Given
        List<ChantierDTO> chantiers = Arrays.asList(chantierDTO);
        when(chantierService.findByClientId(1)).thenReturn(chantiers);

        // When & Then
        mockMvc.perform(get("/api/chantiers/by-client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id_client").value(1));
    }

    @Test
    void testGetChantiersByDateRange_Success() throws Exception {
        // Given
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date debut = sdf.parse("2024-01-01");
        Date fin = sdf.parse("2024-12-31");

        List<ChantierDTO> chantiers = Arrays.asList(chantierDTO, chantierDTO2);
        when(chantierService.findByDateOuvertureBetween(any(Date.class), any(Date.class)))
                .thenReturn(chantiers);

        // When & Then
        mockMvc.perform(get("/api/chantiers/by-date-range")
                        .param("debut", "2024-01-01")
                        .param("fin", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateChantierEtat_Success() throws Exception {
        // Given
        ChantierDTO updatedChantier = new ChantierDTO();
        updatedChantier.setId(1);
        updatedChantier.setEtat(Etat.TERMINE);

        when(chantierService.changerEtatChantier(1, Etat.TERMINE)).thenReturn(updatedChantier);

        // When & Then
        mockMvc.perform(patch("/api/chantiers/1/etat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"TERMINE\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etat").value("TERMINE"));
    }

    @Test
    void testUpdateChantierEtat_NotFound() throws Exception {
        // Given
        when(chantierService.changerEtatChantier(99, Etat.TERMINE))
                .thenThrow(new RuntimeException("Chantier non trouvé"));

        // When & Then
        mockMvc.perform(patch("/api/chantiers/99/etat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"TERMINE\""))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCheckNumPoliceExists_True() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(12345)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/chantiers/check-num-police/12345"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckNumPoliceExists_False() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(99999)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/chantiers/check-num-police/99999"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testCheckTitreFoncierExists_True() throws Exception {
        // Given
        when(chantierService.existsByTitreFoncier("TF123")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/chantiers/check-titre-foncier/TF123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckTitreFoncierExists_False() throws Exception {
        // Given
        when(chantierService.existsByTitreFoncier("TF999")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/chantiers/check-titre-foncier/TF999"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testGetAllChantiers_InternalServerError() throws Exception {
        // Given
        when(chantierService.getAllChantiers())
                .thenThrow(new RuntimeException("Erreur base de données"));

        // When & Then
        mockMvc.perform(get("/api/chantiers"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateChantier_InternalServerError() throws Exception {
        // Given
        when(chantierService.existsByNumPolice(12345)).thenReturn(false);
        when(chantierService.existsByTitreFoncier("TF123")).thenReturn(false);
        when(chantierService.existsByNomProjet("Projet Test")).thenReturn(false);
        when(chantierService.saveChantier(any(ChantierDTO.class)))
                .thenThrow(new RuntimeException("Erreur lors de la sauvegarde"));

        // When & Then
        mockMvc.perform(post("/api/chantiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chantierDTO)))
                .andExpect(status().isInternalServerError());
    }
}