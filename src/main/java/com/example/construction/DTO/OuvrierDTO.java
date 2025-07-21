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

    // Pour les transferts, on utilise des byte[] qui seront converties automatiquement
    private byte[] photoCIN;
    private byte[] photoCNSS;

    // Pour les URLs de visualisation (utiles pour le frontend)
    private String photoCINUrl;
    private String photoCNSSUrl;

    private Type type;
    private float prixHeure;
    private float prixJour;
    private Integer id_chantier;
}
