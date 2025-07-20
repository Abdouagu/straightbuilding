package com.example.construction.service;

import com.example.construction.DTO.UserDTO;
import com.example.construction.DTO.UserRequestDTO;

import java.util.List;

public interface UserService {
    // Méthodes utilisant UserRequestDTO pour les opérations avec mot de passe
    UserDTO createUser(UserRequestDTO requestDTO);
    UserDTO updateUser(int id, UserRequestDTO requestDTO);

    // Méthodes standard sans mot de passe
    void deleteUser(int id);
    UserDTO getUserById(int id);
    UserDTO getUserByUsername(String username);
    UserDTO getUserByEmail(String email); // Nouvelle méthode
    List<UserDTO> getAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void updateLastLogin(int id);
    void enableUser(int id, boolean status);

    // Méthode pour obtenir le mot de passe hashé pour l'authentification
     // Nouvelle méthode
}