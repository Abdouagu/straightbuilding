package com.example.construction.dto;

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
    private String nomProjet;
    private String titreFoncier;
    private Integer numPolice;
    private Date dateOuverture;
    private Date dateContrat;
    private String adresse;
    private Integer duree;
    private Etat etat;
    private float budget;
    private Integer id_client;

}