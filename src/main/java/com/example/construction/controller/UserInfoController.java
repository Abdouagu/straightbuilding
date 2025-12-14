package com.example.construction.controller;

import com.example.construction.dto.UserDTO;
import com.example.construction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserInfoController {

    private final UserService userService;

    @Autowired
    public UserInfoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // L'email est utilisé comme username

            UserDTO user = userService.getUserByEmail(email); // Vous devrez créer cette méthode

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", user.getEmail());
            userInfo.put("username", user.getUsername());
            userInfo.put("role", user.getRole().toString());
            userInfo.put("enabled", user.isEnabled());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}