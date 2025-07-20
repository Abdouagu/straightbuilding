package com.example.construction.service;

import com.example.construction.DTO.ChantierDTO;
import com.example.construction.entities.Etat;
import com.example.construction.entities.chantier;

import java.util.Date;
import java.util.List;

public interface ChantierService {

    ChantierDTO saveChantier(ChantierDTO chantierDTO);
    ChantierDTO getChantierById(int id);
    List<ChantierDTO> getAllChantiers();
    ChantierDTO updateChantier(int id, ChantierDTO chantierDTO);
    void deleteChantier(int id);

    ChantierDTO findByNomProjet(String nomProjet);
    ChantierDTO findByTitreFoncier(String titreFoncier);
    ChantierDTO findByNumPolice(int numPolice);
    List<ChantierDTO> findByEtat(Etat etat);
    List<ChantierDTO> findByClientId(int clientId);
    List<ChantierDTO> findByDateOuvertureBetween(Date debut, Date fin);
    ChantierDTO changerEtatChantier(int id, Etat nouvelEtat);
    boolean existsByNumPolice(int numPolice);
    boolean existsByTitreFoncier(String titreFoncier);
    boolean existsByNomProjet(String nomProjet);
    // Méthodes à ajouter dans votre ChantierService

    boolean existsByNumPoliceAndIdNot(int numPolice, int id) ;

    boolean existsByTitreFoncierAndIdNot(String titreFoncier, int id);

    boolean existsByNomProjetAndIdNot(String nomProjet, int id) ;

}