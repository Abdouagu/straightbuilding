package com.example.construction.controller;

import com.example.construction.DTO.UserDTO;
import com.example.construction.DTO.UserRequestDTO;
import com.example.construction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        // Vérification de l'existence du nom d'utilisateur
        if (userService.existsByUsername(userRequestDTO.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        // Vérification de l'existence de l'email
        if (userService.existsByEmail(userRequestDTO.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        UserDTO createdUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        try {
            UserDTO userDTO = userService.getUserById(id);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        try {
            UserDTO userDTO = userService.getUserByUsername(username);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable int id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            // Vérifier si le nom d'utilisateur mis à jour existe déjà (sauf pour cet utilisateur)
            UserDTO existingUser = userService.getUserById(id);
            if (!existingUser.getUsername().equals(userRequestDTO.getUsername()) &&
                    userService.existsByUsername(userRequestDTO.getUsername())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // Vérifier si l'email mis à jour existe déjà (sauf pour cet utilisateur)
            if (!existingUser.getEmail().equals(userRequestDTO.getEmail()) &&
                    userService.existsByEmail(userRequestDTO.getEmail())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            UserDTO updatedUser = userService.updateUser(id, userRequestDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable int id) {
        try {
            userService.updateLastLogin(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/enable/{status}")
    public ResponseEntity<Void> enableUser(@PathVariable int id, @PathVariable boolean status) {
        try {
            userService.enableUser(id, status);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}