package com.example.construction.repository;

import com.example.construction.entities.Etat;
import com.example.construction.entities.chantier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface Chantierepo extends JpaRepository<chantier, Integer> {
    Optional<chantier> findByNomProjet(String nom_projet);
    Optional<chantier> findByTitreFoncier(String titre_foncier);
    Optional<chantier> findByNumPolice(int num_police);
    List<chantier> findByEtat(Etat etat);
    List<chantier> findByClientId(int id_client);
    List<chantier> findByDateOuvertureBetween(Date debut, Date fin);
    boolean existsByTitreFoncier(String titreFoncier);
    boolean existsByNumPolice(int numPolice);
    boolean existsByNomProjet(String nomProjet);
    // Méthodes à ajouter dans votre ChantierRepository

    // Vérifier si un numéro de police existe pour un autre chantier (pas celui avec l'ID donné)
    boolean existsByNumPoliceAndIdNot(int numPolice, int id);

    // Vérifier si un titre foncier existe pour un autre chantier (pas celui avec l'ID donné)
    boolean existsByTitreFoncierAndIdNot(String titreFoncier, int id);

    // Vérifier si un nom de projet existe pour un autre chantier (pas celui avec l'ID donné)
    boolean existsByNomProjetAndIdNot(String nomProjet, int id);

}