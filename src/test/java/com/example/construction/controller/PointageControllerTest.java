// src/test/java/com/example/construction/controller/PointageControllerTest.java
package com.example.construction.controller;

import com.example.construction.dto.OuvrierDTO;
import com.example.construction.dto.PointageDTO;
import com.example.construction.dto.TableauPointageDTO;
import com.example.construction.entities.Pointage;
import com.example.construction.entities.chantier;
import com.example.construction.entities.ouvrier;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.repository.PointageRepository;
import com.example.construction.service.PointageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointageController.class)
@Import(PointageController.class)
@AutoConfigureMockMvc(addFilters = false)
class PointageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointageService pointageService;

    @MockitoBean
    private PointageRepository pointageRepository;

    @MockitoBean
    private Chantierepo chantierRepository;

    @MockitoBean
    private Ouvrierepo ouvrierRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PointageDTO pointageDTO;
    private TableauPointageDTO tableauPointageDTO;
    private Pointage pointageEntity;
    private chantier chantierEntity;
    private ouvrier ouvrierEntity;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        // Setup entities
        chantierEntity = new chantier();
        chantierEntity.setId(1);
        chantierEntity.setNomProjet("Chantier Test");

        ouvrierEntity = new ouvrier();
        ouvrierEntity.setId(1);
        ouvrierEntity.setNom("Dupont");
        ouvrierEntity.setPrenom("Jean");

        pointageEntity = new Pointage();
        pointageEntity.setId(1L);
        pointageEntity.setOuvrier(ouvrierEntity);
        pointageEntity.setChantier(chantierEntity);
        pointageEntity.setDate(LocalDate.of(2024, 1, 15));
        pointageEntity.setPresent(true);
        pointageEntity.setHeuresTravaillees(9.0f);

        // Setup DTOs
        pointageDTO = new PointageDTO();
        pointageDTO.setId(1L);
        pointageDTO.setOuvrierId(1);
        pointageDTO.setChantierId(1);
        pointageDTO.setDate(LocalDate.of(2024, 1, 15));
        pointageDTO.setPresent(true);
        pointageDTO.setHeuresTravaillees(9.0f);

        tableauPointageDTO = new TableauPointageDTO();
        tableauPointageDTO.setChantierId(1);
        tableauPointageDTO.setDateDebut(LocalDate.of(2024, 1, 1));
        tableauPointageDTO.setDateFin(LocalDate.of(2024, 1, 31));
        tableauPointageDTO.setLignesPointage(Collections.emptyList());
    }

    @Test
    void testCreerPointage_Success() throws Exception {
        // Given
        when(pointageService.creerPointage(any(PointageDTO.class))).thenReturn(pointageDTO);

        // When & Then
        mockMvc.perform(post("/api/pointages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointageDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ouvrierId").value(1))
                .andExpect(jsonPath("$.chantierId").value(1))
                .andExpect(jsonPath("$.present").value(true));
    }

    @Test
    void testCreerPointage_InternalServerError() throws Exception {
        // Given
        when(pointageService.creerPointage(any(PointageDTO.class)))
                .thenThrow(new RuntimeException("Erreur de sauvegarde"));

        // When & Then
        mockMvc.perform(post("/api/pointages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointageDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testModifierPointage_Success() throws Exception {
        // Given
        when(pointageService.modifierPointage(eq(1L), any(PointageDTO.class))).thenReturn(pointageDTO);

        // When & Then
        mockMvc.perform(put("/api/pointages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointageDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testModifierPointage_NotFound() throws Exception {
        // Given
        when(pointageService.modifierPointage(eq(99L), any(PointageDTO.class)))
                .thenThrow(new RuntimeException("Pointage non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/pointages/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointageDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testSupprimerPointage_Success() throws Exception {
        // Given
        doNothing().when(pointageService).supprimerPointage(1L);

        // When & Then
        mockMvc.perform(delete("/api/pointages/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSupprimerPointage_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Pointage non trouvé"))
                .when(pointageService).supprimerPointage(99L);

        // When & Then
        mockMvc.perform(delete("/api/pointages/99"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetPointageById_Success() throws Exception {
        // Given
        when(pointageService.getPointageById(1L)).thenReturn(pointageDTO);

        // When & Then
        mockMvc.perform(get("/api/pointages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ouvrierId").value(1));
    }

    @Test
    void testGetPointageById_NotFound() throws Exception {
        // Given
        when(pointageService.getPointageById(99L))
                .thenThrow(new RuntimeException("Pointage non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/pointages/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPointagesByOuvrierId_Success() throws Exception {
        // Given
        PointageDTO pointage2 = new PointageDTO();
        pointage2.setId(2L);
        pointage2.setOuvrierId(1);

        List<PointageDTO> pointages = Arrays.asList(pointageDTO, pointage2);
        when(pointageService.getPointagesByOuvrierId(1,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)))
                .thenReturn(pointages);

        // When & Then
        mockMvc.perform(get("/api/pointages/ouvrier/1")
                        .param("debut", "2024-01-01")
                        .param("fin", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testGetTableauPointage_Success() throws Exception {
        // Given
        when(pointageService.getTableauPointage(1,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)))
                .thenReturn(tableauPointageDTO);

        // When & Then
        mockMvc.perform(get("/api/pointages/tableau")
                        .param("chantierId", "1")
                        .param("dateDebut", "2024-01-01")
                        .param("dateFin", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chantierId").value(1))
                .andExpect(jsonPath("$.dateDebut").value("2024-01-01"))
                .andExpect(jsonPath("$.dateFin").value("2024-01-31"));
    }

    @Test
    void testSauvegarderTableauPointage_Success() throws Exception {
        // Given - Créer un tableau VALIDE avec des lignes non vides
        TableauPointageDTO validTableau = new TableauPointageDTO();
        validTableau.setChantierId(1);
        validTableau.setDateDebut(LocalDate.of(2024, 1, 1));
        validTableau.setDateFin(LocalDate.of(2024, 1, 31));

        // Créer une ligne de pointage non vide
        TableauPointageDTO.LignePointageDTO ligne = new TableauPointageDTO.LignePointageDTO();

        // Créer un ouvrier DTO minimal
        OuvrierDTO ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(1);
        ouvrierDTO.setNom("Test");
        ouvrierDTO.setPrenom("Ouvrier");
        ligne.setOuvrier(ouvrierDTO);

        // Créer une liste de jours non vide
        List<TableauPointageDTO.JourPointageDTO> jours = new ArrayList<>();
        TableauPointageDTO.JourPointageDTO jour = new TableauPointageDTO.JourPointageDTO();
        jour.setDate(LocalDate.of(2024, 1, 1));
        jour.setPresent(true);
        jour.setHeuresTravaillees(8.0f);
        jours.add(jour);

        ligne.setJours(jours);
        ligne.setPrixHeure(25.0f);
        ligne.setPrixJour(200.0f);
        ligne.setTotalAPayer(200.0f);

        validTableau.setLignesPointage(Arrays.asList(ligne));

        when(pointageService.sauvegarderTableauPointage(any(TableauPointageDTO.class)))
                .thenReturn(validTableau);

        // When & Then
        mockMvc.perform(post("/api/pointages/tableau")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTableau)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chantierId").value(1));
    }

    @Test
    void testSauvegarderTableauPointage_BadRequest_MissingChantierId() throws Exception {
        // Given - Tableau sans chantierId
        TableauPointageDTO invalidTableau = new TableauPointageDTO();
        invalidTableau.setDateDebut(LocalDate.now());
        invalidTableau.setDateFin(LocalDate.now());
        invalidTableau.setLignesPointage(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/pointages/tableau")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTableau)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSauvegarderTableauPointage_BadRequest_MissingDates() throws Exception {
        // Given - Tableau sans dates
        TableauPointageDTO invalidTableau = new TableauPointageDTO();
        invalidTableau.setChantierId(1);
        invalidTableau.setLignesPointage(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/pointages/tableau")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTableau)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSauvegarderTableauPointage_BadRequest_EmptyLignes() throws Exception {
        // Given - Tableau avec lignes vides
        TableauPointageDTO invalidTableau = new TableauPointageDTO();
        invalidTableau.setChantierId(1);
        invalidTableau.setDateDebut(LocalDate.now());
        invalidTableau.setDateFin(LocalDate.now());
        invalidTableau.setLignesPointage(Collections.emptyList());

        // When & Then - Doit retourner 400
        mockMvc.perform(post("/api/pointages/tableau")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTableau)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Aucune ligne de pointage fournie"));
    }

    @Test
    void testCalculerTotalPaiement_Success() throws Exception {
        // Given
        when(pointageService.calculerTotalPaiement(1,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)))
                .thenReturn(1500.0f);

        // When & Then
        mockMvc.perform(get("/api/pointages/paiement/1")
                        .param("dateDebut", "2024-01-01")
                        .param("dateFin", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"));
    }

    @Test
    void testModifierHeuresTravaillees_Success() throws Exception {
        // Given
        PointageDTO updatedPointage = new PointageDTO();
        updatedPointage.setId(1L);
        updatedPointage.setHeuresTravaillees(8.0f);

        when(pointageService.getPointageById(1L)).thenReturn(pointageDTO);
        when(pointageService.modifierPointage(eq(1L), any(PointageDTO.class)))
                .thenReturn(updatedPointage);

        // When & Then
        mockMvc.perform(put("/api/pointages/jour/1")
                        .param("heuresTravaillees", "8.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.heuresTravaillees").value(8.0));
    }

    @Test
    void testTogglePresence_CreateNew() throws Exception {
        // Given - Simuler qu'aucun pointage n'existe
        when(pointageRepository.findByOuvrierIdAndChantierIdAndDate(1, 1, LocalDate.of(2024, 1, 15)))
                .thenReturn(Optional.empty());

        // Simuler la création d'un nouveau pointage via le service
        PointageDTO newPointage = new PointageDTO();
        newPointage.setId(1L);
        newPointage.setOuvrierId(1);
        newPointage.setChantierId(1);
        newPointage.setDate(LocalDate.of(2024, 1, 15));
        newPointage.setPresent(true);
        newPointage.setHeuresTravaillees(9.0f);

        when(pointageService.creerPointage(any(PointageDTO.class))).thenReturn(newPointage);

        // When & Then
        mockMvc.perform(post("/api/pointages/jour/toggle")
                        .param("ouvrierId", "1")
                        .param("chantierId", "1")
                        .param("date", "2024-01-15")
                        .param("heuresTravaillees", "9.0"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.present").value(true));
    }

    @Test
    void testTogglePresence_UpdateExisting() throws Exception {
        // Given - Simuler qu'un pointage existe déjà
        when(pointageRepository.findByOuvrierIdAndChantierIdAndDate(1, 1, LocalDate.of(2024, 1, 15)))
                .thenReturn(Optional.of(pointageEntity));

        // Simuler la modification du pointage existant
        PointageDTO updatedPointage = new PointageDTO();
        updatedPointage.setId(1L);
        updatedPointage.setPresent(false);
        updatedPointage.setHeuresTravaillees(0f);

        when(pointageService.modifierPointage(eq(1L), any(PointageDTO.class)))
                .thenReturn(updatedPointage);

        // When & Then
        mockMvc.perform(post("/api/pointages/jour/toggle")
                        .param("ouvrierId", "1")
                        .param("chantierId", "1")
                        .param("date", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.present").value(false));
    }

    @Test
    void testGetTableauxExistants_Success() throws Exception {
        // Given
        TableauPointageDTO tableau2 = new TableauPointageDTO();
        tableau2.setChantierId(1);
        tableau2.setDateDebut(LocalDate.of(2024, 2, 1));
        tableau2.setDateFin(LocalDate.of(2024, 2, 15));

        List<TableauPointageDTO> tableaux = Arrays.asList(tableauPointageDTO, tableau2);
        when(pointageService.getTableauxExistants(1)).thenReturn(tableaux);

        // When & Then
        mockMvc.perform(get("/api/pointages/tableaux/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].chantierId").value(1))
                .andExpect(jsonPath("$[1].chantierId").value(1));
    }

    @Test
    void testGetTableauxExistants_Empty() throws Exception {
        // Given
        when(pointageService.getTableauxExistants(1)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/pointages/tableaux/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testTogglePresence_InternalServerError() throws Exception {
        // Given
        when(pointageRepository.findByOuvrierIdAndChantierIdAndDate(1, 1, LocalDate.of(2024, 1, 15)))
                .thenThrow(new RuntimeException("Erreur de base de données"));

        // When & Then
        mockMvc.perform(post("/api/pointages/jour/toggle")
                        .param("ouvrierId", "1")
                        .param("chantierId", "1")
                        .param("date", "2024-01-15"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetPointagesByOuvrierId_InternalServerError() throws Exception {
        // Given
        when(pointageService.getPointagesByOuvrierId(1,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)))
                .thenThrow(new RuntimeException("Erreur de récupération"));

        // When & Then
        mockMvc.perform(get("/api/pointages/ouvrier/1")
                        .param("debut", "2024-01-01")
                        .param("fin", "2024-01-31"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testSauvegarderTableauPointage_InternalServerError() throws Exception {
        // Given - Créer un tableau VALIDE avec des lignes non vides
        TableauPointageDTO validTableau = new TableauPointageDTO();
        validTableau.setChantierId(1);
        validTableau.setDateDebut(LocalDate.of(2024, 1, 1));
        validTableau.setDateFin(LocalDate.of(2024, 1, 31));

        // Créer une ligne de pointage non vide
        TableauPointageDTO.LignePointageDTO ligne = new TableauPointageDTO.LignePointageDTO();

        // Créer un ouvrier DTO minimal
        OuvrierDTO ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(1);
        ouvrierDTO.setNom("Test");
        ouvrierDTO.setPrenom("Ouvrier");
        ligne.setOuvrier(ouvrierDTO);

        // Créer une liste de jours non vide
        List<TableauPointageDTO.JourPointageDTO> jours = new ArrayList<>();
        TableauPointageDTO.JourPointageDTO jour = new TableauPointageDTO.JourPointageDTO();
        jour.setDate(LocalDate.of(2024, 1, 1));
        jour.setPresent(true);
        jour.setHeuresTravaillees(8.0f);
        jours.add(jour);

        ligne.setJours(jours);
        ligne.setPrixHeure(25.0f);
        ligne.setPrixJour(200.0f);
        ligne.setTotalAPayer(200.0f);

        validTableau.setLignesPointage(Arrays.asList(ligne));

        when(pointageService.sauvegarderTableauPointage(any(TableauPointageDTO.class)))
                .thenThrow(new RuntimeException("Erreur de sauvegarde"));

        // When & Then
        mockMvc.perform(post("/api/pointages/tableau")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTableau)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testModifierHeuresTravaillees_NotFound() throws Exception {
        // Given
        when(pointageService.getPointageById(1L))
                .thenThrow(new RuntimeException("Pointage non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/pointages/jour/1")
                        .param("heuresTravaillees", "8.0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCalculerTotalPaiement_InternalServerError() throws Exception {
        // Given
        when(pointageService.calculerTotalPaiement(1,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31)))
                .thenThrow(new RuntimeException("Erreur de calcul"));

        // When & Then
        mockMvc.perform(get("/api/pointages/paiement/1")
                        .param("dateDebut", "2024-01-01")
                        .param("dateFin", "2024-01-31"))
                .andExpect(status().isInternalServerError());
    }
}