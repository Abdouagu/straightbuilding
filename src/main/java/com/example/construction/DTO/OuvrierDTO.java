package com.example.construction.DTO;

import com.example.construction.entities.Type;
import com.example.construction.entities.ouvrier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OuvrierDTO {

    private Integer id;
    private String nom;
    private String prenom;
    private String cin;
    private Date dateNaissance;
    private String photoCIN;
    private String photoCNSS;
    private Type type;
    private float prixHeure;
    private float prixJour;
    private Integer id_chantier;

}
