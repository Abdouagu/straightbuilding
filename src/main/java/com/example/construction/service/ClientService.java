package com.example.construction.service;

import com.example.construction.DTO.ClientDTO;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    public List<ClientDTO> getAllClients();
    public ClientDTO getClientById(int id);
    public ClientDTO addClient(ClientDTO clientDTO);
    public ClientDTO updateClient(int id, ClientDTO clientDTO);
    public void deleteClient(int id);
    Optional <ClientDTO> findByEmail(String email);
    Optional <ClientDTO> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
