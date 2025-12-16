// src/test/java/com/example/construction/service/ClientServiceImplTest.java
package com.example.construction.service;

import com.example.construction.dto.ClientDTO;
import com.example.construction.entities.client;
import com.example.construction.repository.Clientrepo;
import com.example.construction.serviceimpl.ClientServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private Clientrepo clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientDTO clientDTO;
    private client clientEntity;

    @BeforeEach
    void setUp() {
        // Setup Client DTO
        clientDTO = new ClientDTO();
        clientDTO.setId(1);
        clientDTO.setNom("Dupont");
        clientDTO.setPrenom("Jean");
        clientDTO.setEmail("jean.dupont@email.com");
        clientDTO.setPhone("0612345678");

        // Setup Client Entity
        clientEntity = new client();
        clientEntity.setId(1);
        clientEntity.setNom("Dupont");
        clientEntity.setPrenom("Jean");
        clientEntity.setEmail("jean.dupont@email.com");
        clientEntity.setPhone("0612345678");
    }

    @Test
    void testAddClient_Success() {
        // Given
        when(clientRepository.save(any(client.class))).thenReturn(clientEntity);

        // When
        ClientDTO result = clientService.addClient(clientDTO);

        // Then
        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals("jean.dupont@email.com", result.getEmail());
        assertEquals(1, result.getId());
        verify(clientRepository, times(1)).save(any(client.class));
    }

    @Test
    void testGetClientById_Found() {
        // Given
        when(clientRepository.findById(1)).thenReturn(Optional.of(clientEntity));

        // When
        ClientDTO result = clientService.getClientById(1);

        // Then
        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals("Jean", result.getPrenom());
        assertEquals(1, result.getId());
    }

    @Test
    void testGetClientById_NotFound() {
        // Given
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            clientService.getClientById(99);
        });

        assertEquals("Client non trouvé avec l'ID: 99", exception.getMessage());
        verify(clientRepository, times(1)).findById(99);
    }

    @Test
    void testGetAllClients() {
        // Given
        client client2 = new client();
        client2.setId(2);
        client2.setNom("Martin");
        client2.setEmail("martin@email.com");

        when(clientRepository.findAll()).thenReturn(Arrays.asList(clientEntity, client2));

        // When
        List<ClientDTO> result = clientService.getAllClients();

        // Then
        assertEquals(2, result.size());
        assertEquals("Dupont", result.get(0).getNom());
        assertEquals("Martin", result.get(1).getNom());
    }

    @Test
    void testUpdateClient_Success() {
        // Given
        ClientDTO updatedDTO = new ClientDTO();
        updatedDTO.setNom("Dupont Modifié");
        updatedDTO.setPrenom("Jean-Paul");
        updatedDTO.setEmail("jeanpaul.dupont@email.com");
        updatedDTO.setPhone("0698765432");

        client existingClient = new client();
        existingClient.setId(1);
        existingClient.setNom("Ancien Nom");
        existingClient.setPrenom("Ancien Prenom");
        existingClient.setEmail("ancien@email.com");
        existingClient.setPhone("0600000000");

        client updatedEntity = new client();
        updatedEntity.setId(1);
        updatedEntity.setNom("Dupont Modifié");
        updatedEntity.setPrenom("Jean-Paul");
        updatedEntity.setEmail("jeanpaul.dupont@email.com");
        updatedEntity.setPhone("0698765432");

        when(clientRepository.findById(1)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(client.class))).thenReturn(updatedEntity);

        // When
        ClientDTO result = clientService.updateClient(1, updatedDTO);

        // Then
        assertNotNull(result);
        assertEquals("Dupont Modifié", result.getNom());
        assertEquals("Jean-Paul", result.getPrenom());
        assertEquals("jeanpaul.dupont@email.com", result.getEmail());
        verify(clientRepository, times(1)).save(any(client.class));
    }

    @Test
    void testUpdateClient_NotFound() {
        // Given
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            clientService.updateClient(99, clientDTO);
        });

        assertEquals("Client non trouvé avec l'ID: 99", exception.getMessage());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testDeleteClient_Success() {
        // Given
        when(clientRepository.existsById(1)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1);

        // When
        clientService.deleteClient(1);

        // Then
        verify(clientRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteClient_NotFound() {
        // Given
        when(clientRepository.existsById(99)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            clientService.deleteClient(99);
        });

        assertEquals("Client non trouvé avec l'ID: 99", exception.getMessage());
        verify(clientRepository, never()).deleteById(anyInt());
    }

    @Test
    void testFindByEmail_Found() {
        // Given
        when(clientRepository.findByEmail("jean.dupont@email.com")).thenReturn(Optional.of(clientEntity));

        // When
        Optional<ClientDTO> result = clientService.findByEmail("jean.dupont@email.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("jean.dupont@email.com", result.get().getEmail());
        assertEquals("Dupont", result.get().getNom());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Given
        when(clientRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        // When
        Optional<ClientDTO> result = clientService.findByEmail("inconnu@email.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByPhone_Found() {
        // Given
        when(clientRepository.findByPhone("0612345678")).thenReturn(Optional.of(clientEntity));

        // When
        Optional<ClientDTO> result = clientService.findByPhone("0612345678");

        // Then
        assertTrue(result.isPresent());
        assertEquals("0612345678", result.get().getPhone());
        assertEquals("Dupont", result.get().getNom());
    }

    @Test
    void testFindByPhone_NotFound() {
        // Given
        when(clientRepository.findByPhone("0999999999")).thenReturn(Optional.empty());

        // When
        Optional<ClientDTO> result = clientService.findByPhone("0999999999");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        // Given
        when(clientRepository.existsByEmail("jean.dupont@email.com")).thenReturn(true);

        // When
        boolean exists = clientService.existsByEmail("jean.dupont@email.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // Given
        when(clientRepository.existsByEmail("inconnu@email.com")).thenReturn(false);

        // When
        boolean exists = clientService.existsByEmail("inconnu@email.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testExistsByPhone_True() {
        // Given
        when(clientRepository.existsByPhone("0612345678")).thenReturn(true);

        // When
        boolean exists = clientService.existsByPhone("0612345678");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByPhone_False() {
        // Given
        when(clientRepository.existsByPhone("0999999999")).thenReturn(false);

        // When
        boolean exists = clientService.existsByPhone("0999999999");

        // Then
        assertFalse(exists);
    }

    @Test
    void testGetAllClients_Empty() {
        // Given
        when(clientRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ClientDTO> result = clientService.getAllClients();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testConversionDTOtoEntity() {
        // Given - clientDTO est déjà configuré dans @BeforeEach

        // When - On teste la conversion interne
        // Note: On ne peut pas tester directement les méthodes privées
        // Mais on peut tester via une méthode publique qui les utilise

        when(clientRepository.save(any(client.class))).thenReturn(clientEntity);
        ClientDTO result = clientService.addClient(clientDTO);

        // Then
        assertNotNull(result);
        assertEquals(clientDTO.getNom(), result.getNom());
        assertEquals(clientDTO.getEmail(), result.getEmail());
    }

    @Test
    void testConversionEntityToDTO() {
        // Given
        when(clientRepository.findById(1)).thenReturn(Optional.of(clientEntity));

        // When
        ClientDTO result = clientService.getClientById(1);

        // Then
        assertNotNull(result);
        assertEquals(clientEntity.getNom(), result.getNom());
        assertEquals(clientEntity.getEmail(), result.getEmail());
        assertEquals(clientEntity.getPhone(), result.getPhone());
    }

    @Test
    void testUpdateClient_PartialUpdate() {
        // Given
        ClientDTO partialUpdate = new ClientDTO();
        partialUpdate.setNom("Nouveau Nom seulement"); // On ne change que le nom

        client existingClient = new client();
        existingClient.setId(1);
        existingClient.setNom("Ancien Nom");
        existingClient.setPrenom("Ancien Prenom");
        existingClient.setEmail("ancien@email.com");
        existingClient.setPhone("0600000000");

        client updatedEntity = new client();
        updatedEntity.setId(1);
        updatedEntity.setNom("Nouveau Nom seulement");
        updatedEntity.setPrenom("Ancien Prenom"); // Reste inchangé
        updatedEntity.setEmail("ancien@email.com"); // Reste inchangé
        updatedEntity.setPhone("0600000000"); // Reste inchangé

        when(clientRepository.findById(1)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(client.class))).thenReturn(updatedEntity);

        // When
        ClientDTO result = clientService.updateClient(1, partialUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Nouveau Nom seulement", result.getNom());
        assertEquals("ancien@email.com", result.getEmail()); // Vérifie que l'email n'a pas changé
    }
}