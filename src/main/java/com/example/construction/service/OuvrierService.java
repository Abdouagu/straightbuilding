package com.example.construction.service;

import com.example.construction.dto.OuvrierDTO;
import com.example.construction.entities.Type;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OuvrierService {

    // Méthode existante
    OuvrierDTO createOuvrier(OuvrierDTO ouvrierDTO);

    // Nouvelle méthode pour créer avec fichiers
    OuvrierDTO createOuvrierWithFiles(OuvrierDTO ouvrierDTO, MultipartFile photoCIN, MultipartFile photoCNSS);

    // Nouvelles méthodes pour récupérer les données d'image
    byte[] getPhotoCINData(int ouvrierId);
    byte[] getPhotoCNSSData(int ouvrierId);
    String getPhotoCINType(int ouvrierId);
    String getPhotoCNSSType(int ouvrierId);

    // Méthodes existantes
    OuvrierDTO findOuvrierById(int id);
    List<OuvrierDTO> getAllOuvriers();
    OuvrierDTO updateOuvrier(int id, OuvrierDTO ouvrierDTO);
    // Ajoutez cette signature dans votre interface OuvrierService

    OuvrierDTO updateOuvrierWithFiles(int id, OuvrierDTO ouvrierDTO, MultipartFile photoCIN, MultipartFile photoCNSS);
    void deleteOuvrier(int id);
    List<OuvrierDTO> findByNom(String nom);
    Optional<OuvrierDTO> findByCin(String cin);
    List<OuvrierDTO> findByType(Type type);
    List<OuvrierDTO> findByChantier(int chantierId);
    boolean existsByCin(String cin);
    Map<String, Integer> getStatsByChantier(int chantierId);
}