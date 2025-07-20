package com.example.construction.service;

import com.example.construction.DTO.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.entities.ouvrier;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OuvrierService {
    OuvrierDTO createOuvrier(OuvrierDTO ouvrierDTO);
    OuvrierDTO findOuvrierById(int id);
    List<OuvrierDTO> getAllOuvriers();
    OuvrierDTO updateOuvrier(int id, OuvrierDTO ouvrierDTO);
    void deleteOuvrier(int id);

    // Méthodes de recherche
    List<OuvrierDTO> findByNom(String nom);
    Optional<OuvrierDTO> findByCin(String cin);
    List<OuvrierDTO> findByType(Type type);
    List<OuvrierDTO> findByChantier(int chantierId);

    // Méthode de vérification
    boolean existsByCin(String cin);

    Map<String, Integer> getStatsByChantier(int chantierId);
}