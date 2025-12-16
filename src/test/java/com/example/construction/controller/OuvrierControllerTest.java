// src/test/java/com/example/construction/controller/OuvrierControllerTest.java
package com.example.construction.controller;

import com.example.construction.dto.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.service.OuvrierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OuvrierController.class)
@Import(OuvrierController.class)
@AutoConfigureMockMvc(addFilters = false)
class OuvrierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OuvrierService ouvrierService;

    @Autowired
    private ObjectMapper objectMapper;

    private OuvrierDTO ouvrierDTO;
    private OuvrierDTO ouvrierDTO2;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(1);
        ouvrierDTO.setNom("Dupont");
        ouvrierDTO.setPrenom("Jean");
        ouvrierDTO.setCin("AB123456");
        ouvrierDTO.setDateNaissance(sdf.parse("1990-01-01"));
        ouvrierDTO.setType(Type.macon); // Enum en minuscules
        ouvrierDTO.setPrixHeure(25.0f);
        ouvrierDTO.setPrixJour(200.0f);
        ouvrierDTO.setId_chantier(1);
        ouvrierDTO.setPhotoCIN("/ouvriers/images/cin/1");
        ouvrierDTO.setPhotoCNSS("/ouvriers/images/cnss/1");
        ouvrierDTO.setPhotoCINName("cin.jpg");
        ouvrierDTO.setPhotoCINType("image/jpeg");
        ouvrierDTO.setPhotoCNSSName("cnss.jpg");
        ouvrierDTO.setPhotoCNSSType("image/jpeg");

        ouvrierDTO2 = new OuvrierDTO();
        ouvrierDTO2.setId(2);
        ouvrierDTO2.setNom("Martin");
        ouvrierDTO2.setPrenom("Pierre");
        ouvrierDTO2.setCin("CD789012");
        ouvrierDTO2.setDateNaissance(sdf.parse("1985-05-15"));
        ouvrierDTO2.setType(Type.greutier); // Enum en minuscules
        ouvrierDTO2.setPrixHeure(30.0f);
        ouvrierDTO2.setPrixJour(240.0f);
        ouvrierDTO2.setId_chantier(1);
        ouvrierDTO2.setPhotoCIN("/ouvriers/images/cin/2");
        ouvrierDTO2.setPhotoCNSS("/ouvriers/images/cnss/2");
    }

    // Tests pour la création d'ouvrier
    @Test
    void testCreateOuvrier_Success() throws Exception {
        // Given
        when(ouvrierService.existsByCin("AB123456")).thenReturn(false);
        when(ouvrierService.createOuvrierWithFiles(any(OuvrierDTO.class), any(), any()))
                .thenReturn(ouvrierDTO);

        // When & Then
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "macon")
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.cin").value("AB123456"))
                .andExpect(jsonPath("$.type").value("macon")); // Corrigé: "macon" en minuscules
    }

    @Test
    void testCreateOuvrier_WithFiles_Success() throws Exception {
        // Given
        when(ouvrierService.existsByCin("AB123456")).thenReturn(false);
        when(ouvrierService.createOuvrierWithFiles(any(OuvrierDTO.class), any(), any()))
                .thenReturn(ouvrierDTO);

        MockMultipartFile photoCIN = new MockMultipartFile(
                "photo_CIN", "cin.jpg", "image/jpeg", new byte[]{1, 2, 3});
        MockMultipartFile photoCNSS = new MockMultipartFile(
                "photo_CNSS", "cnss.jpg", "image/jpeg", new byte[]{4, 5, 6});

        // When & Then
        mockMvc.perform(multipart("/api/ouvriers")
                        .file(photoCIN)
                        .file(photoCNSS)
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "macon")
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("macon")); // Corrigé
    }

    @Test
    void testCreateOuvrier_WithMaçonAccent_Success() throws Exception {
        // Given
        when(ouvrierService.existsByCin("AB123456")).thenReturn(false);
        when(ouvrierService.createOuvrierWithFiles(any(OuvrierDTO.class), any(), any()))
                .thenReturn(ouvrierDTO);

        // When & Then - Le converter gère "maçon" avec accent
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "maçon") // Avec accent
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("macon")); // Mais le JSON retourné est "macon"
    }

    @Test
    void testCreateOuvrier_Greutier_Success() throws Exception {
        // Given
        when(ouvrierService.existsByCin("CD789012")).thenReturn(false);
        when(ouvrierService.createOuvrierWithFiles(any(OuvrierDTO.class), any(), any()))
                .thenReturn(ouvrierDTO2);

        // When & Then
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Martin")
                        .param("prenom", "Pierre")
                        .param("cin", "CD789012")
                        .param("date_naissance", "1985-05-15")
                        .param("type", "greutier")
                        .param("prix_heure", "30.0")
                        .param("prix_jour", "240.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("greutier")); // Corrigé: "greutier" en minuscules
    }

    @Test
    void testCreateOuvrier_ConflictCin() throws Exception {
        // Given
        when(ouvrierService.existsByCin("AB123456")).thenReturn(true);

        // When & Then
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "macon")
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateOuvrier_BadRequestInvalidPrice() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "macon")
                        .param("prix_heure", "invalid")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateOuvrier_InternalServerError() throws Exception {
        // Given
        when(ouvrierService.existsByCin("AB123456")).thenReturn(false);
        when(ouvrierService.createOuvrierWithFiles(any(OuvrierDTO.class), any(), any()))
                .thenThrow(new RuntimeException("Erreur lors de la sauvegarde"));

        // When & Then
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "macon")
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    // Tests pour récupérer un ouvrier
    @Test
    void testGetOuvrierById_Success() throws Exception {
        // Given
        when(ouvrierService.findOuvrierById(1)).thenReturn(ouvrierDTO);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.cin").value("AB123456"))
                .andExpect(jsonPath("$.type").value("macon")) // Corrigé: "macon" en minuscules
                .andExpect(jsonPath("$.photoCIN").value("/ouvriers/images/cin/1"))
                .andExpect(jsonPath("$.photoCNSS").value("/ouvriers/images/cnss/1"));
    }

    @Test
    void testGetOuvrierById_NotFound() throws Exception {
        // Given
        when(ouvrierService.findOuvrierById(99))
                .thenThrow(new RuntimeException("Ouvrier non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/ouvriers/99"))
                .andExpect(status().isNotFound());
    }

    // Tests pour récupérer tous les ouvriers
    @Test
    void testGetAllOuvriers_Success() throws Exception {
        // Given
        List<OuvrierDTO> ouvriers = Arrays.asList(ouvrierDTO, ouvrierDTO2);
        when(ouvrierService.getAllOuvriers()).thenReturn(ouvriers);

        // When & Then
        mockMvc.perform(get("/api/ouvriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Dupont"))
                .andExpect(jsonPath("$[0].type").value("macon")) // Corrigé
                .andExpect(jsonPath("$[1].nom").value("Martin"))
                .andExpect(jsonPath("$[1].type").value("greutier")); // Corrigé
    }

    @Test
    void testGetAllOuvriers_EmptyList() throws Exception {
        // Given
        when(ouvrierService.getAllOuvriers()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/ouvriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllOuvriers_InternalServerError() throws Exception {
        // Given
        when(ouvrierService.getAllOuvriers())
                .thenThrow(new RuntimeException("Erreur base de données"));

        // When & Then
        mockMvc.perform(get("/api/ouvriers"))
                .andExpect(status().isInternalServerError());
    }

    // Tests pour mettre à jour un ouvrier (sans fichiers)
    @Test
    void testUpdateOuvrier_Success() throws Exception {
        // Given
        OuvrierDTO updatedOuvrier = new OuvrierDTO();
        updatedOuvrier.setId(1);
        updatedOuvrier.setNom("Dupont Modifié");
        updatedOuvrier.setPrenom("Jean-Paul");
        updatedOuvrier.setCin("AB123456");
        updatedOuvrier.setType(Type.macon);
        updatedOuvrier.setPrixHeure(26.0f);
        updatedOuvrier.setPrixJour(210.0f);
        updatedOuvrier.setId_chantier(1);

        when(ouvrierService.findOuvrierById(1)).thenReturn(ouvrierDTO);
        when(ouvrierService.existsByCin("AB123456")).thenReturn(false);
        when(ouvrierService.updateOuvrier(eq(1), any(OuvrierDTO.class))).thenReturn(updatedOuvrier);

        // When & Then
        mockMvc.perform(put("/api/ouvriers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOuvrier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Dupont Modifié"))
                .andExpect(jsonPath("$.type").value("macon")); // Corrigé
    }

    @Test
    void testUpdateOuvrier_ConflictCin() throws Exception {
        // Given
        OuvrierDTO updatedOuvrier = new OuvrierDTO();
        updatedOuvrier.setCin("NOUVEAUCIN");
        updatedOuvrier.setType(Type.macon);

        when(ouvrierService.findOuvrierById(1)).thenReturn(ouvrierDTO);
        when(ouvrierService.existsByCin("NOUVEAUCIN")).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/ouvriers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOuvrier)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateOuvrier_NotFound() throws Exception {
        // Given
        when(ouvrierService.findOuvrierById(99))
                .thenThrow(new RuntimeException("Ouvrier non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/ouvriers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ouvrierDTO)))
                .andExpect(status().isNotFound());
    }

    // Tests pour mettre à jour un ouvrier avec fichiers
    @Test
    void testUpdateOuvrierWithFiles_Success() throws Exception {
        // Given
        OuvrierDTO updatedOuvrier = new OuvrierDTO();
        updatedOuvrier.setId(1);
        updatedOuvrier.setNom("Dupont Modifié");

        when(ouvrierService.findOuvrierById(1)).thenReturn(ouvrierDTO);
        when(ouvrierService.existsByCin("AB123456")).thenReturn(false);
        when(ouvrierService.updateOuvrierWithFiles(eq(1), any(OuvrierDTO.class), any(), any()))
                .thenReturn(updatedOuvrier);

        MockMultipartFile photoCIN = new MockMultipartFile(
                "photo_CIN", "cin.jpg", "image/jpeg", new byte[]{1, 2, 3});

        // When & Then
        mockMvc.perform(multipart("/api/ouvriers/{id}", 1)
                        .file(photoCIN)
                        .param("nom", "Dupont Modifié")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "macon")
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Dupont Modifié"));
    }

    // Tests pour supprimer un ouvrier
    @Test
    void testDeleteOuvrier_Success() throws Exception {
        // Given
        doNothing().when(ouvrierService).deleteOuvrier(1);

        // When & Then
        mockMvc.perform(delete("/api/ouvriers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteOuvrier_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Ouvrier non trouvé"))
                .when(ouvrierService).deleteOuvrier(99);

        // When & Then
        mockMvc.perform(delete("/api/ouvriers/99"))
                .andExpect(status().isNotFound());
    }

    // Tests pour les recherches
    @Test
    void testGetOuvriersByNom_Success() throws Exception {
        // Given
        List<OuvrierDTO> ouvriers = Arrays.asList(ouvrierDTO);
        when(ouvrierService.findByNom("Dupont")).thenReturn(ouvriers);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/by-nom/Dupont"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Dupont"))
                .andExpect(jsonPath("$[0].type").value("macon")); // Corrigé
    }

    @Test
    void testGetOuvrierByCin_Success() throws Exception {
        // Given
        when(ouvrierService.findByCin("AB123456")).thenReturn(Optional.of(ouvrierDTO));

        // When & Then
        mockMvc.perform(get("/api/ouvriers/by-cin/AB123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cin").value("AB123456"))
                .andExpect(jsonPath("$.type").value("macon")); // Corrigé
    }

    @Test
    void testGetOuvrierByCin_NotFound() throws Exception {
        // Given
        when(ouvrierService.findByCin("INCONNU")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/ouvriers/by-cin/INCONNU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOuvriersByType_Macon() throws Exception {
        // Given
        List<OuvrierDTO> ouvriers = Arrays.asList(ouvrierDTO);
        when(ouvrierService.findByType(Type.macon)).thenReturn(ouvriers);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/by-type/macon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("macon")); // Corrigé
    }

    @Test
    void testGetOuvriersByType_Greutier() throws Exception {
        // Given
        List<OuvrierDTO> ouvriers = Arrays.asList(ouvrierDTO2);
        when(ouvrierService.findByType(Type.greutier)).thenReturn(ouvriers);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/by-type/greutier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("greutier")); // Corrigé
    }

    @Test
    void testGetOuvriersByChantier_Success() throws Exception {
        // Given
        List<OuvrierDTO> ouvriers = Arrays.asList(ouvrierDTO, ouvrierDTO2);
        when(ouvrierService.findByChantier(1)).thenReturn(ouvriers);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/by-chantier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id_chantier").value(1))
                .andExpect(jsonPath("$[1].id_chantier").value(1));
    }

    // Tests pour vérifier l'existence d'un CIN
    @Test
    void testCheckCinExists_True() throws Exception {
        // Given
        when(ouvrierService.existsByCin("AB123456")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/check-cin/AB123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckCinExists_False() throws Exception {
        // Given
        when(ouvrierService.existsByCin("INCONNU")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/check-cin/INCONNU"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // Tests pour les statistiques
    @Test
    void testGetOuvrierStatsByChantier_Success() throws Exception {
        // Given
        Map<String, Integer> stats = new HashMap<>();
        stats.put("macon", 2);
        stats.put("greutier", 1);
        when(ouvrierService.getStatsByChantier(1)).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/stats/by-chantier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macon").value(2))
                .andExpect(jsonPath("$.greutier").value(1));
    }

    // Tests pour récupérer les types
    @Test
    void testGetTypes_Success() throws Exception {
        // When & Then - Le contrôleur retourne Type.values() qui sont en minuscules
        mockMvc.perform(get("/api/ouvriers/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Type.values().length))
                .andExpect(jsonPath("$[0]").value("ouvrier")) // Corrigé: en minuscules
                .andExpect(jsonPath("$[1]").value("macon")) // Corrigé: en minuscules
                .andExpect(jsonPath("$[2]").value("greutier")) // Corrigé: en minuscules
                .andExpect(jsonPath("$[3]").value("gardien")); // Corrigé: en minuscules
    }

    // Tests pour les images
    @Test
    void testGetPhotoCIN_Success() throws Exception {
        // Given
        byte[] imageData = new byte[]{1, 2, 3};
        when(ouvrierService.getPhotoCINData(1)).thenReturn(imageData);
        when(ouvrierService.getPhotoCINType(1)).thenReturn("image/jpeg");

        // When & Then
        mockMvc.perform(get("/api/ouvriers/images/cin/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"));
    }

    @Test
    void testGetPhotoCIN_NotFound() throws Exception {
        // Given
        when(ouvrierService.getPhotoCINData(1)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/images/cin/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPhotoCNSS_Success() throws Exception {
        // Given
        byte[] imageData = new byte[]{4, 5, 6};
        when(ouvrierService.getPhotoCNSSData(1)).thenReturn(imageData);
        when(ouvrierService.getPhotoCNSSType(1)).thenReturn("image/png");

        // When & Then
        mockMvc.perform(get("/api/ouvriers/images/cnss/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"));
    }

    @Test
    void testGetPhotoCNSS_NotFound() throws Exception {
        // Given
        when(ouvrierService.getPhotoCNSSData(1)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/ouvriers/images/cnss/1"))
                .andExpect(status().isNotFound());
    }

    // Tests pour les erreurs de type invalide
    @Test
    void testCreateOuvrier_InvalidType() throws Exception {
        // When & Then - Type invalide
        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("cin", "AB123456")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "INVALID_TYPE")
                        .param("prix_heure", "25.0")
                        .param("prix_jour", "200.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOuvriersByType_InvalidType() throws Exception {
        // When & Then - Type invalide
        mockMvc.perform(get("/api/ouvriers/by-type/INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }

    // Test supplémentaire pour vérifier le fonctionnement avec tous les types
    @Test
    void testCreateOuvrier_AllTypes() throws Exception {
        // Test avec ouvrier
        when(ouvrierService.existsByCin("EF345678")).thenReturn(false);
        when(ouvrierService.createOuvrierWithFiles(any(OuvrierDTO.class), any(), any()))
                .thenReturn(new OuvrierDTO());

        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Test")
                        .param("prenom", "Ouvrier")
                        .param("cin", "EF345678")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "ouvrier")
                        .param("prix_heure", "20.0")
                        .param("prix_jour", "160.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        // Test avec gardien
        when(ouvrierService.existsByCin("GH901234")).thenReturn(false);

        mockMvc.perform(multipart("/api/ouvriers")
                        .param("nom", "Test")
                        .param("prenom", "Gardien")
                        .param("cin", "GH901234")
                        .param("date_naissance", "1990-01-01")
                        .param("type", "gardien")
                        .param("prix_heure", "18.0")
                        .param("prix_jour", "144.0")
                        .param("id_chantier", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }
}