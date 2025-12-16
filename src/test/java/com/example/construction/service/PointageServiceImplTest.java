// src/test/java/com/example/construction/service/PointageServiceImplTest.java
package com.example.construction.service;

import com.example.construction.dto.OuvrierDTO;
import com.example.construction.dto.PointageDTO;
import com.example.construction.dto.TableauPointageDTO;
import com.example.construction.entities.chantier;
import com.example.construction.entities.ouvrier;
import com.example.construction.entities.Pointage;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.repository.PointageRepository;
import com.example.construction.serviceimpl.PointageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointageServiceImplTest {

    @Mock
    private PointageRepository pointageRepository;

    @Mock
    private Ouvrierepo ouvrierRepository;

    @Mock
    private Chantierepo chantierRepository;

    @Mock
    private OuvrierService ouvrierService;

    @InjectMocks
    private PointageServiceImpl pointageService;

    private PointageDTO pointageDTO;
    private Pointage pointageEntity;
    private ouvrier ouvrierEntity;
    private chantier chantierEntity;
    private OuvrierDTO ouvrierDTO;

    @BeforeEach
    void setUp() {
        // Setup chantier
        chantierEntity = new chantier();
        chantierEntity.setId(1);
        chantierEntity.setNomProjet("Chantier Test");

        // Setup ouvrier entity
        ouvrierEntity = new ouvrier();
        ouvrierEntity.setId(1);
        ouvrierEntity.setNom("Dupont");
        ouvrierEntity.setPrenom("Jean");
        ouvrierEntity.setPrixHeure(25.0f);
        ouvrierEntity.setPrixJour(200.0f);
        ouvrierEntity.setChantier(chantierEntity);

        // Setup ouvrier DTO
        ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(1);
        ouvrierDTO.setNom("Dupont");
        ouvrierDTO.setPrenom("Jean");
        ouvrierDTO.setPrixHeure(25.0f);
        ouvrierDTO.setPrixJour(200.0f);
        ouvrierDTO.setId_chantier(1);

        // Setup pointage entity
        pointageEntity = new Pointage();
        pointageEntity.setId(1L);
        pointageEntity.setOuvrier(ouvrierEntity);
        pointageEntity.setChantier(chantierEntity);
        pointageEntity.setDate(LocalDate.of(2024, 1, 15));
        pointageEntity.setPresent(true);
        pointageEntity.setHeuresTravaillees(9.0f);

        // Setup pointage DTO
        pointageDTO = new PointageDTO();
        pointageDTO.setId(1L);
        pointageDTO.setOuvrierId(1);
        pointageDTO.setChantierId(1);
        pointageDTO.setDate(LocalDate.of(2024, 1, 15));
        pointageDTO.setPresent(true);
        pointageDTO.setHeuresTravaillees(9.0f);
    }

    @Test
    void testCreerPointage_Success() {
        // Given
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierRepository.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(pointageRepository.save(any(Pointage.class))).thenReturn(pointageEntity);

        // Capturer l'argument pour vérification
        ArgumentCaptor<Pointage> pointageCaptor = ArgumentCaptor.forClass(Pointage.class);

        // When
        PointageDTO result = pointageService.creerPointage(pointageDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getOuvrierId());
        assertEquals(1, result.getChantierId());
        assertEquals(LocalDate.of(2024, 1, 15), result.getDate());
        assertTrue(result.isPresent());
        assertEquals(9.0f, result.getHeuresTravaillees());

        verify(pointageRepository).save(pointageCaptor.capture());
        Pointage savedPointage = pointageCaptor.getValue();
        assertEquals(ouvrierEntity, savedPointage.getOuvrier());
        assertEquals(chantierEntity, savedPointage.getChantier());
    }

    @Test
    void testCreerPointage_OuvrierNotFound() {
        // Given
        when(ouvrierRepository.findById(99)).thenReturn(Optional.empty());

        PointageDTO dto = new PointageDTO();
        dto.setOuvrierId(99);
        dto.setChantierId(1);
        dto.setDate(LocalDate.now());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.creerPointage(dto);
        });

        assertTrue(exception.getMessage().contains("Ouvrier non trouvé"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testCreerPointage_ChantierNotFound() {
        // Given
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierRepository.findById(99)).thenReturn(Optional.empty());

        PointageDTO dto = new PointageDTO();
        dto.setOuvrierId(1);
        dto.setChantierId(99);
        dto.setDate(LocalDate.now());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.creerPointage(dto);
        });

        assertTrue(exception.getMessage().contains("Chantier non trouvé"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testModifierPointage_Success() {
        // Given
        PointageDTO updatedDTO = new PointageDTO();
        updatedDTO.setPresent(false);
        updatedDTO.setHeuresTravaillees(0f);

        when(pointageRepository.findById(1L)).thenReturn(Optional.of(pointageEntity));
        when(pointageRepository.save(any(Pointage.class))).thenReturn(pointageEntity);

        // When
        PointageDTO result = pointageService.modifierPointage(1L, updatedDTO);

        // Then
        assertNotNull(result);
        assertEquals(false, pointageEntity.isPresent());
        assertEquals(0f, pointageEntity.getHeuresTravaillees());
        verify(pointageRepository, times(1)).save(pointageEntity);
    }

    @Test
    void testModifierPointage_NotFound() {
        // Given
        when(pointageRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.modifierPointage(99L, pointageDTO);
        });

        assertTrue(exception.getMessage().contains("Pointage non trouvé"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testSupprimerPointage_Success() {
        // Given
        doNothing().when(pointageRepository).deleteById(1L);

        // When
        pointageService.supprimerPointage(1L);

        // Then
        verify(pointageRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetPointageById_Found() {
        // Given
        when(pointageRepository.findById(1L)).thenReturn(Optional.of(pointageEntity));

        // When
        PointageDTO result = pointageService.getPointageById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getOuvrierId());
        assertEquals(1, result.getChantierId());
        assertEquals(LocalDate.of(2024, 1, 15), result.getDate());
        assertTrue(result.isPresent());
        assertEquals(9.0f, result.getHeuresTravaillees());
    }

    @Test
    void testGetPointageById_NotFound() {
        // Given
        when(pointageRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.getPointageById(99L);
        });

        assertTrue(exception.getMessage().contains("Pointage non trouvé"));
    }

    @Test
    void testGetPointagesByOuvrierId() {
        // Given
        LocalDate debut = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);

        Pointage pointage2 = new Pointage();
        pointage2.setId(2L);
        pointage2.setOuvrier(ouvrierEntity);
        pointage2.setDate(LocalDate.of(2024, 1, 20));
        pointage2.setPresent(true);
        pointage2.setHeuresTravaillees(8.0f);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, debut, fin))
                .thenReturn(Arrays.asList(pointageEntity, pointage2));

        // When
        List<PointageDTO> result = pointageService.getPointagesByOuvrierId(1, debut, fin);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testGetPointagesByOuvrierId_Empty() {
        // Given
        LocalDate debut = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, debut, fin))
                .thenReturn(Collections.emptyList());

        // When
        List<PointageDTO> result = pointageService.getPointagesByOuvrierId(1, debut, fin);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTableauPointage() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 15); // Changé à 15 pour inclure le pointage

        // Modifier la date du pointage pour qu'il soit dans la période
        pointageEntity.setDate(LocalDate.of(2024, 1, 10)); // Date dans la période

        when(ouvrierService.findByChantier(1)).thenReturn(Arrays.asList(ouvrierDTO));
        when(pointageRepository.findPointagesPourPeriode(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointageEntity));

        // When
        TableauPointageDTO result = pointageService.getTableauPointage(1, dateDebut, dateFin);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getChantierId());
        assertEquals(dateDebut, result.getDateDebut());
        assertEquals(dateFin, result.getDateFin());
        assertEquals(1, result.getLignesPointage().size());

        // Vérifier les jours
        TableauPointageDTO.LignePointageDTO ligne = result.getLignesPointage().get(0);
        assertEquals(15, ligne.getJours().size()); // Du 1er au 15 janvier = 15 jours

        // Vérifier le jour où il y a pointage
        boolean foundPointageDay = false;
        for (TableauPointageDTO.JourPointageDTO jour : ligne.getJours()) {
            if (jour.getDate().equals(LocalDate.of(2024, 1, 10))) { // Changé à la nouvelle date
                foundPointageDay = true;
                assertTrue(jour.isPresent());
                assertEquals(9.0f, jour.getHeuresTravaillees());
                assertEquals(1L, jour.getPointageId());
            }
        }
        assertTrue(foundPointageDay, "Le jour avec pointage devrait être trouvé");
    }

    @Test
    void testSauvegarderTableauPointage() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 2);

        TableauPointageDTO tableau = new TableauPointageDTO();
        tableau.setChantierId(1);
        tableau.setDateDebut(dateDebut);
        tableau.setDateFin(dateFin);

        // Créer une ligne avec un ouvrier
        TableauPointageDTO.LignePointageDTO ligne = new TableauPointageDTO.LignePointageDTO();
        ligne.setOuvrier(ouvrierDTO);

        // Créer des jours
        List<TableauPointageDTO.JourPointageDTO> jours = new ArrayList<>();

        TableauPointageDTO.JourPointageDTO jour1 = new TableauPointageDTO.JourPointageDTO();
        jour1.setDate(dateDebut);
        jour1.setPresent(true);
        jour1.setHeuresTravaillees(9.0f);
        jour1.setPointageId(null); // Nouveau pointage
        jours.add(jour1);

        TableauPointageDTO.JourPointageDTO jour2 = new TableauPointageDTO.JourPointageDTO();
        jour2.setDate(dateFin);
        jour2.setPresent(false);
        jour2.setHeuresTravaillees(0f);
        jour2.setPointageId(null);
        jours.add(jour2);

        ligne.setJours(jours);
        tableau.setLignesPointage(Arrays.asList(ligne));

        // Mock des appels
        when(ouvrierService.findByChantier(1)).thenReturn(Arrays.asList(ouvrierDTO));
        when(pointageRepository.findPointagesPourPeriode(1, dateDebut, dateFin))
                .thenReturn(Collections.emptyList());
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierRepository.findById(1)).thenReturn(Optional.of(chantierEntity));

        // Mock pour la sauvegarde de pointage
        when(pointageRepository.save(any(Pointage.class))).thenAnswer(invocation -> {
            Pointage pointage = invocation.getArgument(0);
            if (pointage.getId() == null) {
                pointage.setId(99L); // ID généré
            }
            return pointage;
        });

        // When
        TableauPointageDTO result = pointageService.sauvegarderTableauPointage(tableau);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getChantierId());
        verify(pointageRepository, times(1)).save(any(Pointage.class));
    }

    @Test
    void testSauvegarderTableauPointage_UpdateExisting() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 1);

        TableauPointageDTO tableau = new TableauPointageDTO();
        tableau.setChantierId(1);
        tableau.setDateDebut(dateDebut);
        tableau.setDateFin(dateFin);

        // Créer une ligne avec un ouvrier
        TableauPointageDTO.LignePointageDTO ligne = new TableauPointageDTO.LignePointageDTO();
        ligne.setOuvrier(ouvrierDTO);

        // Créer un jour existant avec pointageId
        List<TableauPointageDTO.JourPointageDTO> jours = new ArrayList<>();
        TableauPointageDTO.JourPointageDTO jour1 = new TableauPointageDTO.JourPointageDTO();
        jour1.setDate(dateDebut);
        jour1.setPresent(true);
        jour1.setHeuresTravaillees(8.0f); // Changé de 9h à 8h
        jour1.setPointageId(1L); // Pointage existant
        jours.add(jour1);

        ligne.setJours(jours);
        tableau.setLignesPointage(Arrays.asList(ligne));

        // Mock des appels
        when(ouvrierService.findByChantier(1)).thenReturn(Arrays.asList(ouvrierDTO));
        when(pointageRepository.findPointagesPourPeriode(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointageEntity));
        when(pointageRepository.findById(1L)).thenReturn(Optional.of(pointageEntity));

        // Mock pour la mise à jour
        when(pointageRepository.save(any(Pointage.class))).thenAnswer(invocation -> {
            Pointage pointage = invocation.getArgument(0);
            return pointage;
        });

        // When
        TableauPointageDTO result = pointageService.sauvegarderTableauPointage(tableau);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getChantierId());

        // Vérifier que le pointage a été modifié
        assertEquals(8.0f, pointageEntity.getHeuresTravaillees());
        verify(pointageRepository, times(1)).save(pointageEntity);
    }

    @Test
    void testSauvegarderTableauPointage_DeletePointage() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 1);

        TableauPointageDTO tableau = new TableauPointageDTO();
        tableau.setChantierId(1);
        tableau.setDateDebut(dateDebut);
        tableau.setDateFin(dateFin);

        // Créer une ligne avec un ouvrier
        TableauPointageDTO.LignePointageDTO ligne = new TableauPointageDTO.LignePointageDTO();
        ligne.setOuvrier(ouvrierDTO);

        // Créer un jour non présent avec pointageId existant (pour suppression)
        List<TableauPointageDTO.JourPointageDTO> jours = new ArrayList<>();
        TableauPointageDTO.JourPointageDTO jour1 = new TableauPointageDTO.JourPointageDTO();
        jour1.setDate(dateDebut);
        jour1.setPresent(false); // Non présent -> suppression
        jour1.setPointageId(1L); // Pointage existant à supprimer
        jours.add(jour1);

        ligne.setJours(jours);
        tableau.setLignesPointage(Arrays.asList(ligne));

        // Mock des appels
        when(ouvrierService.findByChantier(1)).thenReturn(Arrays.asList(ouvrierDTO));
        when(pointageRepository.findPointagesPourPeriode(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointageEntity));

        // Mock pour la suppression
        doNothing().when(pointageRepository).deleteById(1L);

        // When
        TableauPointageDTO result = pointageService.sauvegarderTableauPointage(tableau);

        // Then
        assertNotNull(result);
        verify(pointageRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCalculerTotalPaiement() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 31);

        Pointage pointage2 = new Pointage();
        pointage2.setId(2L);
        pointage2.setOuvrier(ouvrierEntity);
        pointage2.setDate(LocalDate.of(2024, 1, 20));
        pointage2.setPresent(true);
        pointage2.setHeuresTravaillees(8.0f);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointageEntity, pointage2));
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        float result = pointageService.calculerTotalPaiement(1, dateDebut, dateFin);

        // Then
        // Jour 1: 9h = prixJour (200) = 200
        // Jour 2: 8h = 8 * 25 = 200
        // Total: 400
        assertEquals(400.0f, result, 0.01);
    }

    @Test
    void testCalculerTotalPaiement_NoPointages() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 31);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, dateDebut, dateFin))
                .thenReturn(Collections.emptyList());
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        float result = pointageService.calculerTotalPaiement(1, dateDebut, dateFin);

        // Then
        assertEquals(0.0f, result, 0.01);
    }

    @Test
    void testCalculerTotalPaiement_OuvrierNotFound() {
        // Given
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 31);

        when(pointageRepository.findByOuvrierIdAndDateBetween(99, dateDebut, dateFin))
                .thenReturn(Collections.emptyList());
        when(ouvrierRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.calculerTotalPaiement(99, dateDebut, dateFin);
        });

        assertTrue(exception.getMessage().contains("Ouvrier non trouvé"));
    }

    @Test
    void testCalculerMontantJournalier_Indirect() {
        // Test via calculerTotalPaiement
        LocalDate dateDebut = LocalDate.of(2024, 1, 1);
        LocalDate dateFin = LocalDate.of(2024, 1, 1);

        // Test avec 8 heures
        Pointage pointage8h = new Pointage();
        pointage8h.setId(1L);
        pointage8h.setOuvrier(ouvrierEntity);
        pointage8h.setDate(dateDebut);
        pointage8h.setPresent(true);
        pointage8h.setHeuresTravaillees(8.0f);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointage8h));
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        float result8h = pointageService.calculerTotalPaiement(1, dateDebut, dateFin);
        assertEquals(200.0f, result8h, 0.01); // 8 * 25 = 200

        // Test avec 9 heures
        when(pointageRepository.findByOuvrierIdAndDateBetween(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointageEntity));

        float result9h = pointageService.calculerTotalPaiement(1, dateDebut, dateFin);
        assertEquals(200.0f, result9h, 0.01); // prixJour = 200

        // Test avec 10 heures
        Pointage pointage10h = new Pointage();
        pointage10h.setId(1L);
        pointage10h.setOuvrier(ouvrierEntity);
        pointage10h.setDate(dateDebut);
        pointage10h.setPresent(true);
        pointage10h.setHeuresTravaillees(10.0f);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointage10h));

        float result10h = pointageService.calculerTotalPaiement(1, dateDebut, dateFin);
        assertEquals(225.0f, result10h, 0.01); // 200 + (1 * 25) = 225

        // Test avec 7 heures
        Pointage pointage7h = new Pointage();
        pointage7h.setId(1L);
        pointage7h.setOuvrier(ouvrierEntity);
        pointage7h.setDate(dateDebut);
        pointage7h.setPresent(true);
        pointage7h.setHeuresTravaillees(7.0f);

        when(pointageRepository.findByOuvrierIdAndDateBetween(1, dateDebut, dateFin))
                .thenReturn(Arrays.asList(pointage7h));

        float result7h = pointageService.calculerTotalPaiement(1, dateDebut, dateFin);
        assertEquals(175.0f, result7h, 0.01); // 7 * 25 = 175
    }

    @Test
    void testGetTableauxExistants() {
        // Given
        Pointage pointage1 = new Pointage();
        pointage1.setId(1L);
        pointage1.setDate(LocalDate.of(2024, 1, 10)); // 10 janvier
        pointage1.setChantier(chantierEntity);
        pointage1.setOuvrier(ouvrierEntity);

        Pointage pointage2 = new Pointage();
        pointage2.setId(2L);
        pointage2.setDate(LocalDate.of(2024, 1, 25)); // 25 janvier (15 jours après)
        pointage2.setChantier(chantierEntity);
        pointage2.setOuvrier(ouvrierEntity);

        when(pointageRepository.findByChantierIdOrderByDateAsc(1))
                .thenReturn(Arrays.asList(pointage1, pointage2));

        // When
        List<TableauPointageDTO> result = pointageService.getTableauxExistants(1);

        // Then
        assertNotNull(result);
        // DEVRAIT ÊTRE 2 ! Car 10 et 25 janvier sont dans des périodes différentes
        assertEquals(2, result.size(), "Devrait créer 2 tableaux pour des dates séparées de plus de 14 jours");

        // Trier par date début pour faciliter les assertions
        result.sort((t1, t2) -> t1.getDateDebut().compareTo(t2.getDateDebut()));

        // Premier tableau (10-24 janvier)
        TableauPointageDTO tableau1 = result.get(0);
        assertEquals(LocalDate.of(2024, 1, 10), tableau1.getDateDebut());
        assertEquals(LocalDate.of(2024, 1, 24), tableau1.getDateFin());

        // Deuxième tableau (25 janvier - 8 février)
        TableauPointageDTO tableau2 = result.get(1);
        assertEquals(LocalDate.of(2024, 1, 25), tableau2.getDateDebut());
        assertEquals(LocalDate.of(2024, 2, 8), tableau2.getDateFin());
    }

    @Test
    void testGetTableauxExistants_Empty() {
        // Given
        when(pointageRepository.findByChantierIdOrderByDateAsc(1))
                .thenReturn(Collections.emptyList());

        // When
        List<TableauPointageDTO> result = pointageService.getTableauxExistants(1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapToEntity_ValidData() {
        // Given
        when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierRepository.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(pointageRepository.save(any(Pointage.class))).thenReturn(pointageEntity);

        // When
        PointageDTO result = pointageService.creerPointage(pointageDTO);

        // Then
        assertNotNull(result);
        verify(ouvrierRepository, times(1)).findById(1);
        verify(chantierRepository, times(1)).findById(1);
        verify(pointageRepository, times(1)).save(any(Pointage.class));
    }

    @Test
    void testMapToEntity_NullOuvrierId() {
        // Given
        PointageDTO dto = new PointageDTO();
        dto.setOuvrierId(null);
        dto.setChantierId(1);
        dto.setDate(LocalDate.now());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.creerPointage(dto);
        });

        assertTrue(exception.getMessage().contains("ID ouvrier manquant"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testMapToEntity_NullChantierId() {
        // Given
        PointageDTO dto = new PointageDTO();
        dto.setOuvrierId(1);
        dto.setChantierId(null);
        dto.setDate(LocalDate.now());

        // Utiliser lenient pour éviter l'erreur de stubbing inutile
        lenient().when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.creerPointage(dto);
        });

        assertTrue(exception.getMessage().contains("ID chantier manquant"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testMapToEntity_ZeroOuvrierId() {
        // Given
        PointageDTO dto = new PointageDTO();
        dto.setOuvrierId(0);
        dto.setChantierId(1);
        dto.setDate(LocalDate.now());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.creerPointage(dto);
        });

        assertTrue(exception.getMessage().contains("ID ouvrier manquant"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testMapToEntity_ZeroChantierId() {
        // Given
        PointageDTO dto = new PointageDTO();
        dto.setOuvrierId(1);
        dto.setChantierId(0);
        dto.setDate(LocalDate.now());

        lenient().when(ouvrierRepository.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointageService.creerPointage(dto);
        });

        assertTrue(exception.getMessage().contains("ID chantier manquant"));
        verify(pointageRepository, never()).save(any(Pointage.class));
    }

    @Test
    void testMapToDTO() {
        // Given
        when(pointageRepository.findById(1L)).thenReturn(Optional.of(pointageEntity));

        // When
        PointageDTO result = pointageService.getPointageById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getOuvrierId());
        assertEquals(1, result.getChantierId());
        assertEquals(LocalDate.of(2024, 1, 15), result.getDate());
        assertTrue(result.isPresent());
        assertEquals(9.0f, result.getHeuresTravaillees());
    }

    @Test
    void testMapToDTO_NullOuvrier() {
        // Given
        Pointage pointageWithoutOuvrier = new Pointage();
        pointageWithoutOuvrier.setId(2L);
        pointageWithoutOuvrier.setChantier(chantierEntity);
        pointageWithoutOuvrier.setDate(LocalDate.now());
        pointageWithoutOuvrier.setPresent(true);
        pointageWithoutOuvrier.setHeuresTravaillees(8.0f);
        // ouvrier est null

        when(pointageRepository.findById(2L)).thenReturn(Optional.of(pointageWithoutOuvrier));

        // When
        PointageDTO result = pointageService.getPointageById(2L);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertNull(result.getOuvrierId()); // ouvrierId devrait être null
        assertEquals(1, result.getChantierId());
    }

    @Test
    void testMapToDTO_NullChantier() {
        // Given
        Pointage pointageWithoutChantier = new Pointage();
        pointageWithoutChantier.setId(3L);
        pointageWithoutChantier.setOuvrier(ouvrierEntity);
        pointageWithoutChantier.setDate(LocalDate.now());
        pointageWithoutChantier.setPresent(true);
        pointageWithoutChantier.setHeuresTravaillees(8.0f);
        // chantier est null

        when(pointageRepository.findById(3L)).thenReturn(Optional.of(pointageWithoutChantier));

        // When
        PointageDTO result = pointageService.getPointageById(3L);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(1, result.getOuvrierId());
        assertNull(result.getChantierId()); // chantierId devrait être null
    }
}