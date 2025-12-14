package com.example.construction.controller;

import com.example.construction.dto.ClientDTO;
import com.example.construction.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        // Vérification de l'existence de l'email
        if (clientService.existsByEmail(clientDTO.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Vérification de l'existence du numéro de téléphone
        if (clientService.existsByPhone(clientDTO.getPhone())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        ClientDTO createdClient = clientService.addClient(clientDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable int id) {
        try {
            ClientDTO clientDTO = clientService.getClientById(id);
            return ResponseEntity.ok(clientDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable int id, @RequestBody ClientDTO clientDTO) {
        try {
            // Vérifier si l'email mis à jour existe déjà (sauf pour ce client)
            ClientDTO existingClient = clientService.getClientById(id);
            if (!existingClient.getEmail().equals(clientDTO.getEmail()) &&
                    clientService.existsByEmail(clientDTO.getEmail())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // Vérifier si le numéro de téléphone mis à jour existe déjà (sauf pour ce client)
            if (!existingClient.getPhone().equals(clientDTO.getPhone()) &&
                    clientService.existsByPhone(clientDTO.getPhone())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            ClientDTO updatedClient = clientService.updateClient(id, clientDTO);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable int id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<ClientDTO> getClientByEmail(@PathVariable String email) {
        return clientService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<ClientDTO> getClientByPhone(@PathVariable String phone) {
        return clientService.findByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = clientService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-phone/{phone}")
    public ResponseEntity<Boolean> checkPhoneExists(@PathVariable String phone) {
        boolean exists = clientService.existsByPhone(phone);
        return ResponseEntity.ok(exists);
    }
}