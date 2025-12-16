// src/test/java/com/example/construction/service/ChantierServiceImplTest.java

package com.example.construction.service;

import com.example.construction.dto.ChantierDTO;
import com.example.construction.entities.Etat;
import com.example.construction.entities.chantier;
import com.example.construction.entities.client;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Clientrepo;
import com.example.construction.serviceimpl.ChantierServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // SEULEMENT cette annotation
class ChantierServiceImplTest {

    @Mock
    private Chantierepo chantierRepository;

    @Mock
    private Clientrepo clientRepository;

    @InjectMocks
    private ChantierServiceImpl chantierService;

    private ChantierDTO chantierDTO;
    private chantier chantierEntity;
    private client clientEntity;

    @BeforeEach
    void setUp() {
        // Setup d'un client
        clientEntity = new client();
        clientEntity.setId(1);
        clientEntity.setNom("Client Test");
        clientEntity.setEmail("client@test.com");

        // Setup d'un chantier DTO
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

        // Setup d'un chantier Entity
        chantierEntity = new chantier();
        chantierEntity.setId(1);
        chantierEntity.setNomProjet("Projet Test");
        chantierEntity.setTitreFoncier("TF123");
        chantierEntity.setNumPolice(12345);
        chantierEntity.setDateOuverture(new Date());
        chantierEntity.setDateContrat(new Date());
        chantierEntity.setAdresse("123 Rue Test");
        chantierEntity.setDuree(180);
        chantierEntity.setEtat(Etat.EN_COURS);
        chantierEntity.setBudget(100000.0f);
        chantierEntity.setClient(clientEntity);
    }

    @Test
    void testSaveChantier_Success() {
        // Given
        when(clientRepository.findById(1)).thenReturn(Optional.of(clientEntity));
        when(chantierRepository.save(any(chantier.class))).thenReturn(chantierEntity);

        // When
        ChantierDTO result = chantierService.saveChantier(chantierDTO);

        // Then
        assertNotNull(result);
        assertEquals("Projet Test", result.getNomProjet());
        assertEquals(Etat.EN_COURS, result.getEtat());
        assertEquals(1, result.getId());
        verify(chantierRepository, times(1)).save(any(chantier.class));
    }

    @Test
    void testSaveChantier_ClientNotFound() {
        // Given
        when(clientRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            chantierService.saveChantier(chantierDTO);
        });

        assertEquals("Client non trouvé avec l'ID: 1", exception.getMessage());
        verify(chantierRepository, never()).save(any());
    }

    @Test
    void testGetChantierById_Found() {
        // Given
        when(chantierRepository.findById(1)).thenReturn(Optional.of(chantierEntity));

        // When
        ChantierDTO result = chantierService.getChantierById(1);

        // Then
        assertNotNull(result);
        assertEquals("Projet Test", result.getNomProjet());
        assertEquals(1, result.getId_client());
    }

    @Test
    void testGetChantierById_NotFound() {
        // Given
        when(chantierRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            chantierService.getChantierById(99);
        });

        assertEquals("Chantier non trouvé avec l'ID: 99", exception.getMessage());
    }

    @Test
    void testGetAllChantiers() {
        // Given
        chantier chantier2 = new chantier();
        chantier2.setId(2);
        chantier2.setNomProjet("Projet Test 2");

        when(chantierRepository.findAll()).thenReturn(Arrays.asList(chantierEntity, chantier2));

        // When
        List<ChantierDTO> result = chantierService.getAllChantiers();

        // Then
        assertEquals(2, result.size());
        assertEquals("Projet Test", result.get(0).getNomProjet());
        assertEquals("Projet Test 2", result.get(1).getNomProjet());
    }

    @Test
    void testUpdateChantier_Success() {
        // Given
        ChantierDTO updatedDTO = new ChantierDTO();
        updatedDTO.setNomProjet("Projet Modifié");
        updatedDTO.setTitreFoncier("TF456");
        updatedDTO.setNumPolice(67890);
        updatedDTO.setAdresse("456 Nouvelle Rue");
        updatedDTO.setDuree(200);
        updatedDTO.setEtat(Etat.TERMINE);
        updatedDTO.setBudget(150000.0f);
        updatedDTO.setId_client(1);

        chantier existingChantier = new chantier();
        existingChantier.setId(1);
        existingChantier.setNomProjet("Ancien Projet");
        existingChantier.setClient(clientEntity);

        when(chantierRepository.findById(1)).thenReturn(Optional.of(existingChantier));
        when(clientRepository.findById(1)).thenReturn(Optional.of(clientEntity));
        when(chantierRepository.save(any(chantier.class))).thenAnswer(invocation -> {
            chantier saved = invocation.getArgument(0);
            saved.setId(1);
            return saved;
        });

        // When
        ChantierDTO result = chantierService.updateChantier(1, updatedDTO);

        // Then
        assertNotNull(result);
        assertEquals("Projet Modifié", result.getNomProjet());
        assertEquals(Etat.TERMINE, result.getEtat());
        assertEquals(150000.0f, result.getBudget(), 0.001);
        verify(chantierRepository, times(1)).save(any(chantier.class));
    }

    @Test
    void testDeleteChantier_Success() {
        // Given
        when(chantierRepository.existsById(1)).thenReturn(true);
        doNothing().when(chantierRepository).deleteById(1);

        // When
        chantierService.deleteChantier(1);

        // Then
        verify(chantierRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteChantier_NotFound() {
        // Given
        when(chantierRepository.existsById(99)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            chantierService.deleteChantier(99);
        });

        assertEquals("Chantier non trouvé avec l'ID: 99", exception.getMessage());
        verify(chantierRepository, never()).deleteById(anyInt());
    }

    @Test
    void testFindByEtat() {
        // Given
        chantier chantier2 = new chantier();
        chantier2.setId(2);
        chantier2.setEtat(Etat.EN_COURS);

        when(chantierRepository.findByEtat(Etat.EN_COURS))
                .thenReturn(Arrays.asList(chantierEntity, chantier2));

        // When
        List<ChantierDTO> result = chantierService.findByEtat(Etat.EN_COURS);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getEtat() == Etat.EN_COURS));
    }

    @Test
    void testFindByClientId() {
        // Given
        chantier chantier2 = new chantier();
        chantier2.setId(2);
        chantier2.setClient(clientEntity);

        when(chantierRepository.findByClientId(1))
                .thenReturn(Arrays.asList(chantierEntity, chantier2));

        // When
        List<ChantierDTO> result = chantierService.findByClientId(1);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getId_client() == 1));
    }

    @Test
    void testChangerEtatChantier() {
        // Given
        when(chantierRepository.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(chantierRepository.save(any(chantier.class))).thenReturn(chantierEntity);

        // When
        ChantierDTO result = chantierService.changerEtatChantier(1, Etat.TERMINE);

        // Then
        assertEquals(Etat.TERMINE, result.getEtat());
        verify(chantierRepository, times(1)).save(chantierEntity);
    }

    @Test
    void testExistsByNumPolice() {
        // Given
        when(chantierRepository.existsByNumPolice(12345)).thenReturn(true);

        // When
        boolean exists = chantierService.existsByNumPolice(12345);

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByNumPoliceAndIdNot() {
        // Given
        when(chantierRepository.existsByNumPoliceAndIdNot(12345, 1)).thenReturn(false);

        // When
        boolean exists = chantierService.existsByNumPoliceAndIdNot(12345, 1);

        // Then
        assertFalse(exists);
    }

    // Ajoute ces tests supplémentaires pour couvrir plus de méthodes :

    @Test
    void testFindByNomProjet() {
        // Given
        when(chantierRepository.findByNomProjet("Projet Test")).thenReturn(Optional.of(chantierEntity));

        // When
        ChantierDTO result = chantierService.findByNomProjet("Projet Test");

        // Then
        assertNotNull(result);
        assertEquals("Projet Test", result.getNomProjet());
    }

    @Test
    void testFindByTitreFoncier() {
        // Given
        when(chantierRepository.findByTitreFoncier("TF123")).thenReturn(Optional.of(chantierEntity));

        // When
        ChantierDTO result = chantierService.findByTitreFoncier("TF123");

        // Then
        assertNotNull(result);
        assertEquals("TF123", result.getTitreFoncier());
    }

    @Test
    void testFindByNumPolice() {
        // Given
        when(chantierRepository.findByNumPolice(12345)).thenReturn(Optional.of(chantierEntity));

        // When
        ChantierDTO result = chantierService.findByNumPolice(12345);

        // Then
        assertNotNull(result);
        assertEquals(12345, result.getNumPolice());
    }

    @Test
    void testFindByDateOuvertureBetween() {
        // Given
        Date debut = new Date();
        Date fin = new Date();

        when(chantierRepository.findByDateOuvertureBetween(debut, fin))
                .thenReturn(Arrays.asList(chantierEntity));

        // When
        List<ChantierDTO> result = chantierService.findByDateOuvertureBetween(debut, fin);

        // Then
        assertEquals(1, result.size());
        assertEquals("Projet Test", result.get(0).getNomProjet());
    }

    @Test
    void testExistsByTitreFoncier() {
        // Given
        when(chantierRepository.existsByTitreFoncier("TF123")).thenReturn(true);

        // When
        boolean exists = chantierService.existsByTitreFoncier("TF123");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByNomProjet() {
        // Given
        when(chantierRepository.existsByNomProjet("Projet Test")).thenReturn(true);

        // When
        boolean exists = chantierService.existsByNomProjet("Projet Test");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByTitreFoncierAndIdNot() {
        // Given
        when(chantierRepository.existsByTitreFoncierAndIdNot("TF123", 1)).thenReturn(true);

        // When
        boolean exists = chantierService.existsByTitreFoncierAndIdNot("TF123", 1);

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByNomProjetAndIdNot() {
        // Given
        when(chantierRepository.existsByNomProjetAndIdNot("Projet Test", 1)).thenReturn(true);

        // When
        boolean exists = chantierService.existsByNomProjetAndIdNot("Projet Test", 1);

        // Then
        assertTrue(exists);
    }
}