package com.example.construction.repository;

import com.example.construction.entities.Pointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {

    // Trouver les pointages par Ouvrier et par plage de dates
    List<Pointage> findByOuvrierIdAndDateBetween(Integer ouvrierId, LocalDate debut, LocalDate fin);


    // Trouver les pointages pour un chantier entre 2 dates
    @Query("SELECT p FROM Pointage p WHERE p.chantier.id = :chantierId AND p.date BETWEEN :dateDebut AND :dateFin")
    List<Pointage> findPointagesPourPeriode(
            @Param("chantierId") int chantierId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);


    @Query("SELECT p FROM Pointage p WHERE p.ouvrier.id = :ouvrierId AND p.chantier.id = :chantierId AND p.date = :date")
    Optional<Pointage> findByOuvrierIdAndChantierIdAndDate(
            @Param("ouvrierId") Integer ouvrierId,
            @Param("chantierId") Integer chantierId,
            @Param("date") LocalDate date);




    // Dans PointageRepository.java - Ajouter cette nouvelle méthode

    @Query("SELECT p FROM Pointage p WHERE p.chantier.id = :chantierId ORDER BY p.date ASC")
    List<Pointage> findByChantierIdOrderByDateAsc(@Param("chantierId") int chantierId);



// Supprimer la méthode privée filtrerLundis qui n'est plus nécessaire
}