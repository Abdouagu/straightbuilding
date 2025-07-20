package com.example.construction.service;


import com.example.construction.DTO.PointageDTO;
import com.example.construction.DTO.TableauPointageDTO;

import java.time.LocalDate;
import java.util.List;

public interface PointageService {
    PointageDTO creerPointage(PointageDTO pointageDTO);

    PointageDTO modifierPointage(Long id, PointageDTO pointageDTO);

    void supprimerPointage(Long id);

    PointageDTO getPointageById(Long id);

    List<PointageDTO> getPointagesByOuvrierId(int ouvrierId, LocalDate debut, LocalDate fin);

    TableauPointageDTO getTableauPointage(int chantierId, LocalDate dateDebut, LocalDate dateFin);

    TableauPointageDTO sauvegarderTableauPointage(TableauPointageDTO tableauPointageDTO);

    float calculerTotalPaiement(int ouvrierId, LocalDate dateDebut, LocalDate dateFin);

    List<TableauPointageDTO> getTableauxExistants(int chantierId);

}
