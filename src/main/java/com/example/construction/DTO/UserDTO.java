package com.example.construction.DTO;


import com.example.construction.entities.role;
import com.example.construction.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id_user;
    private String username;
    private role role;
    private String email;
    private boolean enabled;
    private Date last_login;
}
