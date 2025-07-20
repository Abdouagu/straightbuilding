package com.example.construction.DTO;

import com.example.construction.entities.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClientDTO {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String phone;
}
