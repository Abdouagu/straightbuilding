package com.example.construction.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="AppUser")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_user;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private role role;

    @Column(nullable = false,unique = true)
    private String email;

    @Column( nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private Date last_login;
}
