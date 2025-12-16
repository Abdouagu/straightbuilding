// src/test/java/com/example/construction/service/OuvrierServiceImplTest.java
package com.example.construction.service;

import com.example.construction.dto.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.entities.chantier;
import com.example.construction.entities.ouvrier;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.serviceimpl.OuvrierServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OuvrierServiceImplTest {

    @Mock
    private Ouvrierepo ouvrierepo;

    @Mock
    private Chantierepo chantierepo;

    @InjectMocks
    private OuvrierServiceImpl ouvrierService;

    private OuvrierDTO ouvrierDTO;
    private ouvrier ouvrierEntity;
    private chantier chantierEntity;

    @BeforeEach
    void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Setup chantier
        chantierEntity = new chantier();
        chantierEntity.setId(1);
        chantierEntity.setNomProjet("Chantier Test");

        // Setup ouvrier entity
        ouvrierEntity = new ouvrier();
        ouvrierEntity.setId(1);
        ouvrierEntity.setNom("Dupont");
        ouvrierEntity.setPrenom("Jean");
        ouvrierEntity.setCin("AB123456");
        ouvrierEntity.setDateNaissance(sdf.parse("1990-01-01"));
        ouvrierEntity.setType(Type.macon);
        ouvrierEntity.setPrixHeure(25.0f);
        ouvrierEntity.setPrixJour(200.0f);
        ouvrierEntity.setChantier(chantierEntity);
        ouvrierEntity.setPhotoCINData(new byte[]{1, 2, 3});
        ouvrierEntity.setPhotoCINName("cin.jpg");
        ouvrierEntity.setPhotoCINType("image/jpeg");
        ouvrierEntity.setPhotoCNSSData(new byte[]{4, 5, 6});
        ouvrierEntity.setPhotoCNSSName("cnss.jpg");
        ouvrierEntity.setPhotoCNSSType("image/jpeg");

        // Setup ouvrier DTO
        ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(1);
        ouvrierDTO.setNom("Dupont");
        ouvrierDTO.setPrenom("Jean");
        ouvrierDTO.setCin("AB123456");
        ouvrierDTO.setDateNaissance(sdf.parse("1990-01-01"));
        ouvrierDTO.setType(Type.macon);
        ouvrierDTO.setPrixHeure(25.0f);
        ouvrierDTO.setPrixJour(200.0f);
        ouvrierDTO.setId_chantier(1);
        ouvrierDTO.setPhotoCINName("cin.jpg");
        ouvrierDTO.setPhotoCINType("image/jpeg");
        ouvrierDTO.setPhotoCNSSName("cnss.jpg");
        ouvrierDTO.setPhotoCNSSType("image/jpeg");
    }

    @Test
    void testCreateOuvrier_Success() {
        // Given
        when(chantierepo.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(ouvrierEntity);

        // When
        OuvrierDTO result = ouvrierService.createOuvrier(ouvrierDTO);

        // Then
        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals("Jean", result.getPrenom());
        assertEquals("AB123456", result.getCin());
        assertEquals(Type.macon, result.getType());
        assertEquals(25.0f, result.getPrixHeure(), 0.001);
        assertEquals(200.0f, result.getPrixJour(), 0.001);
        assertEquals(1, result.getId_chantier());
        assertNotNull(result.getPhotoCIN());
        assertNotNull(result.getPhotoCNSS());
        verify(ouvrierepo, times(1)).save(any(ouvrier.class));
    }

    @Test
    void testCreateOuvrier_ChantierNotFound() {
        // Given
        when(chantierepo.findById(1)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ouvrierService.createOuvrier(ouvrierDTO);
        });

        assertEquals("Chantier non trouvé avec l'ID: 1", exception.getMessage());
        verify(ouvrierepo, never()).save(any(ouvrier.class));
    }

    @Test
    void testCreateOuvrierWithFiles_Success() throws IOException {
        // Given
        MockMultipartFile photoCIN = new MockMultipartFile(
                "photo_CIN", "cin.jpg", "image/jpeg", new byte[]{1, 2, 3});
        MockMultipartFile photoCNSS = new MockMultipartFile(
                "photo_CNSS", "cnss.jpg", "image/jpeg", new byte[]{4, 5, 6});

        when(chantierepo.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(ouvrierEntity);

        // When
        OuvrierDTO result = ouvrierService.createOuvrierWithFiles(ouvrierDTO, photoCIN, photoCNSS);

        // Then
        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals(1, result.getId());
        verify(ouvrierepo, times(1)).save(any(ouvrier.class));
    }

    @Test
    void testCreateOuvrierWithFiles_IOException() throws IOException {
        // Given
        MultipartFile mockPhoto = mock(MultipartFile.class);
        when(mockPhoto.isEmpty()).thenReturn(false);
        when(mockPhoto.getBytes()).thenThrow(new IOException("Erreur de lecture"));

        when(chantierepo.findById(1)).thenReturn(Optional.of(chantierEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ouvrierService.createOuvrierWithFiles(ouvrierDTO, mockPhoto, null);
        });

        assertEquals("Erreur lors de la lecture des fichiers uploadés", exception.getMessage());
        verify(ouvrierepo, never()).save(any(ouvrier.class));
    }

    @Test
    void testFindOuvrierById_Found() {
        // Given
        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        OuvrierDTO result = ouvrierService.findOuvrierById(1);

        // Then
        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals("Jean", result.getPrenom());
        assertEquals("AB123456", result.getCin());
        assertEquals(Type.macon, result.getType());
        assertEquals("/ouvriers/images/cin/1", result.getPhotoCIN());
        assertEquals("/ouvriers/images/cnss/1", result.getPhotoCNSS());
    }

    @Test
    void testFindOuvrierById_NotFound() {
        // Given
        when(ouvrierepo.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ouvrierService.findOuvrierById(99);
        });

        assertEquals("Ouvrier non trouvé avec l'ID: 99", exception.getMessage());
    }

    @Test
    void testGetAllOuvriers() {
        // Given
        ouvrier ouvrier2 = new ouvrier();
        ouvrier2.setId(2);
        ouvrier2.setNom("Martin");
        ouvrier2.setType(Type.greutier);

        when(ouvrierepo.findAll()).thenReturn(Arrays.asList(ouvrierEntity, ouvrier2));

        // When
        List<OuvrierDTO> result = ouvrierService.getAllOuvriers();

        // Then
        assertEquals(2, result.size());
        assertEquals("Dupont", result.get(0).getNom());
        assertEquals(Type.macon, result.get(0).getType());
        assertEquals("Martin", result.get(1).getNom());
        assertEquals(Type.greutier, result.get(1).getType());
    }

    @Test
    void testUpdateOuvrier_Success() {
        // Given
        OuvrierDTO updatedDTO = new OuvrierDTO();
        updatedDTO.setNom("Dupont Modifié");
        updatedDTO.setPrenom("Jean-Paul");
        updatedDTO.setCin("AB123456"); // Même CIN
        updatedDTO.setType(Type.greutier);
        updatedDTO.setPrixHeure(30.0f);
        updatedDTO.setPrixJour(240.0f);
        updatedDTO.setId_chantier(1);

        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierepo.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(ouvrierEntity);

        // When
        OuvrierDTO result = ouvrierService.updateOuvrier(1, updatedDTO);

        // Then
        assertNotNull(result);
        verify(ouvrierepo, times(1)).save(any(ouvrier.class));
    }

    @Test
    void testUpdateOuvrier_ChantierChange() {
        // Given
        chantier newChantier = new chantier();
        newChantier.setId(2);
        newChantier.setNomProjet("Nouveau Chantier");

        OuvrierDTO updatedDTO = new OuvrierDTO();
        updatedDTO.setNom("Dupont");
        updatedDTO.setId_chantier(2);

        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierepo.findById(2)).thenReturn(Optional.of(newChantier));
        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(ouvrierEntity);

        // When
        OuvrierDTO result = ouvrierService.updateOuvrier(1, updatedDTO);

        // Then
        assertNotNull(result);
        verify(chantierepo, times(1)).findById(2);
    }

    @Test
    void testUpdateOuvrier_NotFound() {
        // Given
        when(ouvrierepo.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ouvrierService.updateOuvrier(99, ouvrierDTO);
        });

        assertEquals("Ouvrier non trouvé avec l'ID: 99", exception.getMessage());
    }

    @Test
    void testUpdateOuvrierWithFiles_Success() throws IOException {
        // Given
        MockMultipartFile newPhotoCIN = new MockMultipartFile(
                "photo_CIN", "new_cin.jpg", "image/jpeg", new byte[]{7, 8, 9});

        OuvrierDTO updatedDTO = new OuvrierDTO();
        updatedDTO.setNom("Dupont Modifié");
        updatedDTO.setCin("AB123456");
        updatedDTO.setType(Type.macon);
        updatedDTO.setId_chantier(1);
        updatedDTO.setPhotoCINName("new_cin.jpg");
        updatedDTO.setPhotoCINType("image/jpeg");

        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierepo.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(ouvrierEntity);

        // When
        OuvrierDTO result = ouvrierService.updateOuvrierWithFiles(1, updatedDTO, newPhotoCIN, null);

        // Then
        assertNotNull(result);
        verify(ouvrierepo, times(1)).save(any(ouvrier.class));
    }

    @Test
    void testUpdateOuvrierWithFiles_WithoutNewFiles() {
        // Given
        OuvrierDTO updatedDTO = new OuvrierDTO();
        updatedDTO.setNom("Dupont Modifié");
        updatedDTO.setCin("AB123456");
        updatedDTO.setType(Type.macon);
        updatedDTO.setId_chantier(1);
        updatedDTO.setPhotoCINName("cin.jpg"); // Garder le même nom
        updatedDTO.setPhotoCINType("image/jpeg");

        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));
        when(chantierepo.findById(1)).thenReturn(Optional.of(chantierEntity));
        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(ouvrierEntity);

        // When
        OuvrierDTO result = ouvrierService.updateOuvrierWithFiles(1, updatedDTO, null, null);

        // Then
        assertNotNull(result);
        verify(ouvrierepo, times(1)).save(any(ouvrier.class));
    }

    @Test
    void testDeleteOuvrier_Success() {
        // Given
        when(ouvrierepo.existsById(1)).thenReturn(true);
        doNothing().when(ouvrierepo).deleteById(1);

        // When
        ouvrierService.deleteOuvrier(1);

        // Then
        verify(ouvrierepo, times(1)).deleteById(1);
    }

    @Test
    void testDeleteOuvrier_NotFound() {
        // Given
        when(ouvrierepo.existsById(99)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ouvrierService.deleteOuvrier(99);
        });

        assertEquals("Ouvrier non trouvé avec l'ID: 99", exception.getMessage());
        verify(ouvrierepo, never()).deleteById(anyInt());
    }

    @Test
    void testFindByNom() {
        // Given
        ouvrier ouvrier2 = new ouvrier();
        ouvrier2.setId(2);
        ouvrier2.setNom("Dupond"); // Similaire

        when(ouvrierepo.findByNomContainingIgnoreCase("Dupont"))
                .thenReturn(Arrays.asList(ouvrierEntity, ouvrier2));

        // When
        List<OuvrierDTO> result = ouvrierService.findByNom("Dupont");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(o -> o.getNom().contains("Dupont")));
        assertTrue(result.stream().anyMatch(o -> o.getNom().contains("Dupond")));
    }

    @Test
    void testFindByCin_Found() {
        // Given
        when(ouvrierepo.findByCin("AB123456")).thenReturn(Optional.of(ouvrierEntity));

        // When
        Optional<OuvrierDTO> result = ouvrierService.findByCin("AB123456");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Dupont", result.get().getNom());
        assertEquals("AB123456", result.get().getCin());
    }

    @Test
    void testFindByCin_NotFound() {
        // Given
        when(ouvrierepo.findByCin("INCONNU")).thenReturn(Optional.empty());

        // When
        Optional<OuvrierDTO> result = ouvrierService.findByCin("INCONNU");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByType() {
        // Given
        ouvrier ouvrier2 = new ouvrier();
        ouvrier2.setId(2);
        ouvrier2.setType(Type.macon);

        when(ouvrierepo.findByType(Type.macon))
                .thenReturn(Arrays.asList(ouvrierEntity, ouvrier2));

        // When
        List<OuvrierDTO> result = ouvrierService.findByType(Type.macon);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> o.getType() == Type.macon));
    }

    @Test
    void testFindByChantier() {
        // Given
        ouvrier ouvrier2 = new ouvrier();
        ouvrier2.setId(2);
        ouvrier2.setChantier(chantierEntity);

        when(ouvrierepo.findByChantier_Id(1))
                .thenReturn(Arrays.asList(ouvrierEntity, ouvrier2));

        // When
        List<OuvrierDTO> result = ouvrierService.findByChantier(1);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> o.getId_chantier() == 1));
    }

    @Test
    void testExistsByCin_True() {
        // Given
        when(ouvrierepo.existsByCin("AB123456")).thenReturn(true);

        // When
        boolean exists = ouvrierService.existsByCin("AB123456");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByCin_False() {
        // Given
        when(ouvrierepo.existsByCin("INCONNU")).thenReturn(false);

        // When
        boolean exists = ouvrierService.existsByCin("INCONNU");

        // Then
        assertFalse(exists);
    }

    @Test
    void testGetStatsByChantier() {
        // Given
        ouvrier ouvrier2 = new ouvrier();
        ouvrier2.setId(2);
        ouvrier2.setType(Type.greutier);
        ouvrier2.setChantier(chantierEntity);

        ouvrier ouvrier3 = new ouvrier();
        ouvrier3.setId(3);
        ouvrier3.setType(Type.macon);
        ouvrier3.setChantier(chantierEntity);

        when(ouvrierepo.findByChantier_Id(1))
                .thenReturn(Arrays.asList(ouvrierEntity, ouvrier2, ouvrier3));

        // When
        Map<String, Integer> stats = ouvrierService.getStatsByChantier(1);

        // Then
        assertEquals(2, stats.size());
        assertEquals(2, stats.get("macon")); // 2 maçons
        assertEquals(1, stats.get("greutier")); // 1 gréutier
    }

    @Test
    void testGetStatsByChantier_Empty() {
        // Given
        when(ouvrierepo.findByChantier_Id(99)).thenReturn(Collections.emptyList());

        // When
        Map<String, Integer> stats = ouvrierService.getStatsByChantier(99);

        // Then
        assertTrue(stats.isEmpty());
    }

    @Test
    void testGetPhotoCINData() {
        // Given
        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        byte[] result = ouvrierService.getPhotoCINData(1);

        // Then
        assertNotNull(result);
        assertArrayEquals(new byte[]{1, 2, 3}, result);
    }

    @Test
    void testGetPhotoCNSSData() {
        // Given
        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        byte[] result = ouvrierService.getPhotoCNSSData(1);

        // Then
        assertNotNull(result);
        assertArrayEquals(new byte[]{4, 5, 6}, result);
    }

    @Test
    void testGetPhotoCINType() {
        // Given
        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        String result = ouvrierService.getPhotoCINType(1);

        // Then
        assertEquals("image/jpeg", result);
    }

    @Test
    void testGetPhotoCNSSType() {
        // Given
        when(ouvrierepo.findById(1)).thenReturn(Optional.of(ouvrierEntity));

        // When
        String result = ouvrierService.getPhotoCNSSType(1);

        // Then
        assertEquals("image/jpeg", result);
    }

    @Test
    void testGetPhotoData_NotFound() {
        // Given
        when(ouvrierepo.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ouvrierService.getPhotoCINData(99);
        });

        assertEquals("Ouvrier non trouvé avec l'ID: 99", exception.getMessage());
    }


    @Test
    void testConvertToDTO_WithoutPhotos() {
        // Given
        ouvrier ouvrierWithoutPhotos = new ouvrier();
        ouvrierWithoutPhotos.setId(3);
        ouvrierWithoutPhotos.setNom("SansPhotos");
        ouvrierWithoutPhotos.setPrenom("Test");
        ouvrierWithoutPhotos.setCin("TEST123");
        ouvrierWithoutPhotos.setDateNaissance(new Date());
        ouvrierWithoutPhotos.setType(Type.gardien);
        ouvrierWithoutPhotos.setPrixHeure(20.0f);
        ouvrierWithoutPhotos.setPrixJour(160.0f);
        ouvrierWithoutPhotos.setChantier(chantierEntity);
        // Pas de photos (photoCINData et photoCNSSData sont null)

        // When - Tester via findById (méthode qui utilise convertToDTO)
        when(ouvrierepo.findById(3)).thenReturn(Optional.of(ouvrierWithoutPhotos));

        // Then
        OuvrierDTO result = ouvrierService.findOuvrierById(3);

        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("SansPhotos", result.getNom());
        assertEquals("Test", result.getPrenom());
        assertEquals("TEST123", result.getCin());
        assertEquals(Type.gardien, result.getType());
        assertEquals(20.0f, result.getPrixHeure(), 0.001);
        assertEquals(160.0f, result.getPrixJour(), 0.001);
        assertNull(result.getPhotoCIN()); // Doit être null car pas de photo CIN
        assertNull(result.getPhotoCNSS()); // Doit être null car pas de photo CNSS
        assertEquals(1, result.getId_chantier()); // Doit avoir le chantier
    }

    @Test
    void testConvertToDTO_WithoutChantier() {
        // Given
        ouvrier ouvrierWithoutChantier = new ouvrier();
        ouvrierWithoutChantier.setId(4);
        ouvrierWithoutChantier.setNom("SansChantier");
        ouvrierWithoutChantier.setType(Type.ouvrier);
        // Pas de chantier

        // When - Tester indirectement via getAllOuvriers
        when(ouvrierepo.findAll()).thenReturn(Collections.singletonList(ouvrierWithoutChantier));

        // Then
        List<OuvrierDTO> result = ouvrierService.getAllOuvriers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SansChantier", result.get(0).getNom());
        assertNull(result.get(0).getId_chantier());
    }

    @Test
    void testCreateOuvrier_WithoutChantier() {
        // Given
        OuvrierDTO dtoWithoutChantier = new OuvrierDTO();
        dtoWithoutChantier.setId(5);
        dtoWithoutChantier.setNom("SansChantierDTO");
        dtoWithoutChantier.setType(Type.ouvrier);
        // Pas de chantier

        ouvrier savedEntity = new ouvrier();
        savedEntity.setId(5);
        savedEntity.setNom("SansChantierDTO");
        savedEntity.setType(Type.ouvrier);

        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(savedEntity);

        // When
        OuvrierDTO result = ouvrierService.createOuvrier(dtoWithoutChantier);

        // Then
        assertNotNull(result);
        assertEquals("SansChantierDTO", result.getNom());
        verify(chantierepo, never()).findById(anyInt());
    }

    @Test
    void testCreateOuvrier_NullChantierId() {
        // Given
        OuvrierDTO dtoWithNullChantier = new OuvrierDTO();
        dtoWithNullChantier.setNom("Test");
        dtoWithNullChantier.setType(Type.ouvrier);
        dtoWithNullChantier.setId_chantier(null); // null chantier ID

        ouvrier savedEntity = new ouvrier();
        savedEntity.setId(6);
        savedEntity.setNom("Test");
        savedEntity.setType(Type.ouvrier);

        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(savedEntity);

        // When
        OuvrierDTO result = ouvrierService.createOuvrier(dtoWithNullChantier);

        // Then
        assertNotNull(result);
        assertEquals("Test", result.getNom());
        verify(chantierepo, never()).findById(anyInt());
    }

    @Test
    void testCreateOuvrier_ZeroChantierId() {
        // Given
        OuvrierDTO dtoWithZeroChantier = new OuvrierDTO();
        dtoWithZeroChantier.setNom("Test");
        dtoWithZeroChantier.setType(Type.ouvrier);
        dtoWithZeroChantier.setId_chantier(0); // 0 chantier ID

        ouvrier savedEntity = new ouvrier();
        savedEntity.setId(7);
        savedEntity.setNom("Test");
        savedEntity.setType(Type.ouvrier);

        when(ouvrierepo.save(any(ouvrier.class))).thenReturn(savedEntity);

        // When
        OuvrierDTO result = ouvrierService.createOuvrier(dtoWithZeroChantier);

        // Then
        assertNotNull(result);
        assertEquals("Test", result.getNom());
        verify(chantierepo, never()).findById(anyInt());
    }
}