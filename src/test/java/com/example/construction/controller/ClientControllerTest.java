// src/test/java/com/example/construction/controller/ClientControllerTest.java
package com.example.construction.controller;

import com.example.construction.dto.ClientDTO;
import com.example.construction.service.ClientService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@Import(ClientController.class)
@AutoConfigureMockMvc(addFilters = false) // Même configuration que ChantierControllerTest
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientDTO clientDTO;
    private ClientDTO clientDTO2;

    @BeforeEach
    void setUp() {
        clientDTO = new ClientDTO();
        clientDTO.setId(1);
        clientDTO.setNom("Dupont");
        clientDTO.setPrenom("Jean");
        clientDTO.setEmail("jean.dupont@email.com");
        clientDTO.setPhone("0612345678");

        clientDTO2 = new ClientDTO();
        clientDTO2.setId(2);
        clientDTO2.setNom("Martin");
        clientDTO2.setPrenom("Pierre");
        clientDTO2.setEmail("pierre.martin@email.com");
        clientDTO2.setPhone("0698765432");
    }

    @Test
    void testCreateClient_Success() throws Exception {
        // Given
        when(clientService.existsByEmail("jean.dupont@email.com")).thenReturn(false);
        when(clientService.existsByPhone("0612345678")).thenReturn(false);
        when(clientService.addClient(any(ClientDTO.class))).thenReturn(clientDTO);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
    }

    @Test
    void testCreateClient_ConflictEmail() throws Exception {
        // Given
        when(clientService.existsByEmail("jean.dupont@email.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateClient_ConflictPhone() throws Exception {
        // Given
        when(clientService.existsByEmail("jean.dupont@email.com")).thenReturn(false);
        when(clientService.existsByPhone("0612345678")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateClient_InternalServerError() throws Exception {
        // Given
        when(clientService.existsByEmail("jean.dupont@email.com")).thenReturn(false);
        when(clientService.existsByPhone("0612345678")).thenReturn(false);
        when(clientService.addClient(any(ClientDTO.class)))
                .thenThrow(new RuntimeException("Erreur lors de la sauvegarde"));

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetClientById_Success() throws Exception {
        // Given
        when(clientService.getClientById(1)).thenReturn(clientDTO);

        // When & Then
        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
    }

    @Test
    void testGetClientById_NotFound() throws Exception {
        // Given
        when(clientService.getClientById(99))
                .thenThrow(new RuntimeException("Client non trouvé"));

        // When & Then
        mockMvc.perform(get("/api/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllClients_Success() throws Exception {
        // Given
        List<ClientDTO> clients = Arrays.asList(clientDTO, clientDTO2);
        when(clientService.getAllClients()).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Dupont"))
                .andExpect(jsonPath("$[1].nom").value("Martin"));
    }

    @Test
    void testGetAllClients_EmptyList() throws Exception {
        // Given
        when(clientService.getAllClients()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllClients_InternalServerError() throws Exception {
        // Given
        when(clientService.getAllClients())
                .thenThrow(new RuntimeException("Erreur base de données"));

        // When & Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateClient_Success() throws Exception {
        // Given
        ClientDTO existingClient = new ClientDTO();
        existingClient.setId(1);
        existingClient.setEmail("ancien@email.com");
        existingClient.setPhone("0600000000");
        existingClient.setNom("Ancien Nom");
        existingClient.setPrenom("Ancien Prenom");

        ClientDTO updatedClient = new ClientDTO();
        updatedClient.setId(1);
        updatedClient.setNom("Dupont Modifié");
        updatedClient.setPrenom("Jean-Paul");
        updatedClient.setEmail("nouveau@email.com");
        updatedClient.setPhone("0612345678");

        when(clientService.getClientById(1)).thenReturn(existingClient);
        when(clientService.existsByEmail("nouveau@email.com")).thenReturn(false);
        when(clientService.existsByPhone("0612345678")).thenReturn(false);
        when(clientService.updateClient(eq(1), any(ClientDTO.class))).thenReturn(updatedClient);

        // When & Then
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Dupont Modifié"))
                .andExpect(jsonPath("$.email").value("nouveau@email.com"));
    }

    @Test
    void testUpdateClient_ConflictEmail() throws Exception {
        // Given
        ClientDTO existingClient = new ClientDTO();
        existingClient.setId(1);
        existingClient.setEmail("ancien@email.com");
        existingClient.setPhone("0600000000");
        existingClient.setNom("Ancien Nom");
        existingClient.setPrenom("Ancien Prenom");

        when(clientService.getClientById(1)).thenReturn(existingClient);
        when(clientService.existsByEmail("nouveau@email.com")).thenReturn(true);

        // Prepare updated client with new email
        ClientDTO updatedClient = new ClientDTO();
        updatedClient.setEmail("nouveau@email.com");
        updatedClient.setNom("Dupont");
        updatedClient.setPrenom("Jean");

        // When & Then
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateClient_ConflictPhone() throws Exception {
        // Given
        ClientDTO existingClient = new ClientDTO();
        existingClient.setId(1);
        existingClient.setEmail("ancien@email.com");
        existingClient.setPhone("0600000000");
        existingClient.setNom("Ancien Nom");
        existingClient.setPrenom("Ancien Prenom");

        when(clientService.getClientById(1)).thenReturn(existingClient);
        when(clientService.existsByEmail("ancien@email.com")).thenReturn(false);
        when(clientService.existsByPhone("0612345678")).thenReturn(true);

        // Prepare updated client with new phone
        ClientDTO updatedClient = new ClientDTO();
        updatedClient.setEmail("ancien@email.com");
        updatedClient.setPhone("0612345678");
        updatedClient.setNom("Dupont");
        updatedClient.setPrenom("Jean");

        // When & Then
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateClient_NoConflictWhenEmailUnchanged() throws Exception {
        // Given
        ClientDTO existingClient = new ClientDTO();
        existingClient.setId(1);
        existingClient.setEmail("jean.dupont@email.com");
        existingClient.setPhone("0612345678");

        ClientDTO updatedClient = new ClientDTO();
        updatedClient.setId(1);
        updatedClient.setNom("Dupont Modifié");
        updatedClient.setEmail("jean.dupont@email.com"); // Même email
        updatedClient.setPhone("0612345678"); // Même phone

        when(clientService.getClientById(1)).thenReturn(existingClient);
        // Note: existsByEmail et existsByPhone ne sont PAS appelés car les emails/phones sont identiques
        when(clientService.updateClient(eq(1), any(ClientDTO.class))).thenReturn(updatedClient);

        // When & Then
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClient)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateClient_NotFound() throws Exception {
        // Given
        when(clientService.getClientById(99))
                .thenThrow(new RuntimeException("Client non trouvé"));

        // When & Then
        mockMvc.perform(put("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteClient_Success() throws Exception {
        // Given
        doNothing().when(clientService).deleteClient(1);

        // When & Then
        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteClient_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Client non trouvé"))
                .when(clientService).deleteClient(99);

        // When & Then
        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientByEmail_Success() throws Exception {
        // Given
        when(clientService.findByEmail("jean.dupont@email.com"))
                .thenReturn(Optional.of(clientDTO));

        // When & Then
        mockMvc.perform(get("/api/clients/by-email/jean.dupont@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
    }

    @Test
    void testGetClientByEmail_NotFound() throws Exception {
        // Given
        when(clientService.findByEmail("inconnu@email.com"))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/clients/by-email/inconnu@email.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientByPhone_Success() throws Exception {
        // Given
        when(clientService.findByPhone("0612345678"))
                .thenReturn(Optional.of(clientDTO));

        // When & Then
        mockMvc.perform(get("/api/clients/by-phone/0612345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("0612345678"));
    }

    @Test
    void testGetClientByPhone_NotFound() throws Exception {
        // Given
        when(clientService.findByPhone("0999999999"))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/clients/by-phone/0999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCheckEmailExists_True() throws Exception {
        // Given
        when(clientService.existsByEmail("jean.dupont@email.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/clients/check-email/jean.dupont@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckEmailExists_False() throws Exception {
        // Given
        when(clientService.existsByEmail("inconnu@email.com")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/clients/check-email/inconnu@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testCheckPhoneExists_True() throws Exception {
        // Given
        when(clientService.existsByPhone("0612345678")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/clients/check-phone/0612345678"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCheckPhoneExists_False() throws Exception {
        // Given
        when(clientService.existsByPhone("0999999999")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/clients/check-phone/0999999999"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void testUpdateClient_PartialData() throws Exception {
        // Given
        ClientDTO existingClient = new ClientDTO();
        existingClient.setId(1);
        existingClient.setEmail("ancien@email.com");
        existingClient.setPhone("0600000000");
        existingClient.setNom("Ancien Nom");
        existingClient.setPrenom("Ancien Prenom");

        ClientDTO partialUpdate = new ClientDTO();
        partialUpdate.setNom("Nouveau Nom seulement"); // On ne change que le nom

        ClientDTO updatedClient = new ClientDTO();
        updatedClient.setId(1);
        updatedClient.setNom("Nouveau Nom seulement");
        updatedClient.setPrenom("Ancien Prenom");
        updatedClient.setEmail("ancien@email.com");
        updatedClient.setPhone("0600000000");

        when(clientService.getClientById(1)).thenReturn(existingClient);
        when(clientService.updateClient(eq(1), any(ClientDTO.class))).thenReturn(updatedClient);

        // When & Then
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nouveau Nom seulement"))
                .andExpect(jsonPath("$.email").value("ancien@email.com"));
    }
}