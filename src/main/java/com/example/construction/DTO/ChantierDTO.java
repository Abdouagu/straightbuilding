package com.example.construction.DTO;

import com.example.construction.entities.Etat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChantierDTO {
    private Integer id;
    private String NomProjet;
    private String TitreFoncier;
    private Integer NumPolice;
    private Date DateOuverture;
    private Date DateContrat;
    private String Adresse;
    private Integer Duree;
    private Etat etat;
    private float budget;
    private Integer id_client;

}