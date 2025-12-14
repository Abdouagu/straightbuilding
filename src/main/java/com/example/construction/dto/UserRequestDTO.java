package com.example.construction.dto;

import com.example.construction.entities.role;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String username;
    private String password;  // Champ uniquement pour la création/modification
    private role role;
    private String email;
    private boolean enabled;
}