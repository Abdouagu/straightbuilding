package com.example.construction.serviceimpl;

import com.example.construction.DTO.UserDTO;
import com.example.construction.DTO.UserRequestDTO;
import com.example.construction.entities.user;
import com.example.construction.repository.Userepo;
import com.example.construction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Userepo userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(Userepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(UserRequestDTO requestDTO) {
        // Créer une nouvelle entité user à partir du DTO
        user newUser = new user();
        newUser.setUsername(requestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(requestDTO.getPassword())); // Crypter le mot de passe
        newUser.setRole(requestDTO.getRole());
        newUser.setEmail(requestDTO.getEmail());
        newUser.setEnabled(true);
        newUser.setLast_login(new Date());

        // Sauvegarder l'utilisateur
        user savedUser = userRepository.save(newUser);

        // Convertir et retourner le DTO
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(int id, UserRequestDTO requestDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(requestDTO.getUsername());
                    if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
                    }
                    existingUser.setRole(requestDTO.getRole());
                    existingUser.setEmail(requestDTO.getEmail());
                    existingUser.setEnabled(requestDTO.isEnabled());

                    return convertToDTO(userRepository.save(existingUser));
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getUserById(int id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le nom: " + username));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
    }



    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updateLastLogin(int id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setLast_login(new Date());
            userRepository.save(user);
        });
    }

    @Override
    public void enableUser(int id, boolean status) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(status);
            userRepository.save(user);
        });
    }

    // Méthode utilitaire pour convertir user en UserDTO
    private UserDTO convertToDTO(user entity) {
        UserDTO dto = new UserDTO();
        dto.setId_user(entity.getId_user());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setEnabled(entity.isEnabled());
        dto.setRole(entity.getRole());
        dto.setLast_login(entity.getLast_login());
        return dto;
    }
}