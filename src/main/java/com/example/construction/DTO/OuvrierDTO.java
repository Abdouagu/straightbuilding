package com.example.construction.DTO;

import com.example.construction.entities.Type;

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

    // Pour les photos, on garde des références pour l'affichage
    private String photoCIN; // URL pour récupérer l'image
    private String photoCNSS; // URL pour récupérer l'image

    // Ajout des métadonnées des photos
    private String photoCINName;
    private String photoCINType;
    private String photoCNSSName;
    private String photoCNSSType;

    private Type type;
    private float prixHeure;
    private float prixJour;
    private Integer id_chantier;

}
