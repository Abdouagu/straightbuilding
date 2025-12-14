package com.example.construction.serviceimpl;

import com.example.construction.dto.ClientDTO;
import com.example.construction.entities.client;
import com.example.construction.repository.Clientrepo;
import com.example.construction.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private Clientrepo clientRepository;

    // Méthode pour convertir l'entité en dto
    private ClientDTO convertToDTO(client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        return dto;
    }

    // Méthode pour convertir le dto en entité
    private client convertToEntity(ClientDTO clientDTO) {
        client entity = new client();
        entity.setId(clientDTO.getId());
        entity.setNom(clientDTO.getNom());
        entity.setPrenom(clientDTO.getPrenom());
        entity.setEmail(clientDTO.getEmail());
        entity.setPhone(clientDTO.getPhone());
        return entity;
    }

    @Override
    public ClientDTO addClient (ClientDTO clientDTO) {
        client entity = convertToEntity(clientDTO);
        client savedEntity = clientRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public ClientDTO getClientById(int id) {
        client entity = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + id));
        return convertToDTO(entity);
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO updateClient(int id, ClientDTO clientDTO) {
        // Vérifier si le client existe
        client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + id));

        // Mise à jour des champs
        existingClient.setNom(clientDTO.getNom());
        existingClient.setPrenom(clientDTO.getPrenom());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setPhone(clientDTO.getPhone());

        // Sauvegarde de l'entité mise à jour
        client updatedEntity = clientRepository.save(existingClient);

        // Retourner le dto mis à jour
        return convertToDTO(updatedEntity);
    }

    @Override
    public void deleteClient(int id) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client non trouvé avec l'ID: " + id);
        }
        clientRepository.deleteById(id);
    }

    @Override
    public Optional<ClientDTO> findByEmail(String email) {
        return clientRepository.findByEmail(email).map(this::convertToDTO);

    }

    @Override
    public Optional<ClientDTO> findByPhone(String phone) {
        return clientRepository.findByPhone(phone).map(this::convertToDTO);

    }

    @Override
    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {return clientRepository.existsByPhone(phone);}
}