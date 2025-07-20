package com.example.construction.serviceimpl;

import com.example.construction.DTO.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.entities.chantier;
import com.example.construction.entities.client;
import com.example.construction.entities.ouvrier;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.service.OuvrierService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional

public class OuvrierServiceImpl implements OuvrierService {
    @Autowired
    private Ouvrierepo ouvrierepo;

    @Autowired
    private Chantierepo chantierepo;


    private OuvrierDTO convertToDTO (ouvrier ouvrier) {
        OuvrierDTO ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(ouvrier.getId());
        ouvrierDTO.setNom(ouvrier.getNom());
        ouvrierDTO.setPrenom(ouvrier.getPrenom());
        ouvrierDTO.setCin(ouvrier.getCin());
        ouvrierDTO.setDateNaissance(ouvrier.getDateNaissance());
        ouvrierDTO.setPhotoCIN(ouvrier.getPhotoCIN());
        ouvrierDTO.setPhotoCNSS(ouvrier.getPhotoCNSS());
        ouvrierDTO.setType(ouvrier.getType());
        ouvrierDTO.setPrixHeure(ouvrier.getPrixHeure());
        ouvrierDTO.setPrixJour(ouvrier.getPrixJour());


        if (ouvrier.getChantier() != null) {
            ouvrierDTO.setId_chantier(ouvrier.getChantier().getId());
        }
        return ouvrierDTO;
    }

    private ouvrier converTOEntity (OuvrierDTO ouvrierDTO) {
        ouvrier entity = new ouvrier();
        entity.setId(ouvrierDTO.getId());
        entity.setNom(ouvrierDTO.getNom());
        entity.setPrenom(ouvrierDTO.getPrenom());
        entity.setCin(ouvrierDTO.getCin());
        entity.setDateNaissance(ouvrierDTO.getDateNaissance());
        entity.setType(ouvrierDTO.getType());
        entity.setPhotoCIN(ouvrierDTO.getPhotoCIN());
        entity.setPhotoCNSS(ouvrierDTO.getPhotoCNSS());
        entity.setPrixHeure(ouvrierDTO.getPrixHeure());
        entity.setPrixJour(ouvrierDTO.getPrixJour());

        if (ouvrierDTO.getId_chantier() > 0) {
            chantier chantier = chantierepo.findById(ouvrierDTO.getId_chantier())
                    .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec l'ID: " + ouvrierDTO.getId_chantier()));
            entity.setChantier(chantier);
        }

        return entity;

    }

    @Override
    public OuvrierDTO createOuvrier(OuvrierDTO ouvrierDTO) {
        ouvrier entity = converTOEntity(ouvrierDTO);
        return convertToDTO(ouvrierepo.save(entity));
    }

    @Override
    public OuvrierDTO findOuvrierById(int id) {
        ouvrier entity=ouvrierepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ouvrier non trouve avec l'ID"+id));
                return convertToDTO(entity);
    }

    @Override
    public List<OuvrierDTO> getAllOuvriers() {
        return ouvrierepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OuvrierDTO updateOuvrier(int id, OuvrierDTO ouvrierDTO) {
        ouvrier existingOuvrier = ouvrierepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec l'ID: " + id));

        // Mise à jour des champs modifiés
        existingOuvrier.setNom(ouvrierDTO.getNom());
        existingOuvrier.setPrenom(ouvrierDTO.getPrenom());
        existingOuvrier.setCin(ouvrierDTO.getCin());
        existingOuvrier.setDateNaissance(ouvrierDTO.getDateNaissance());
        existingOuvrier.setPhotoCIN(ouvrierDTO.getPhotoCIN());
        existingOuvrier.setPhotoCNSS(ouvrierDTO.getPhotoCNSS());
        existingOuvrier.setType(ouvrierDTO.getType());
        existingOuvrier.setPrixHeure(ouvrierDTO.getPrixHeure());
        existingOuvrier.setPrixJour(ouvrierDTO.getPrixJour());

        // Mise à jour du chantier si nécessaire
        if (ouvrierDTO.getId_chantier() != null) {
            Optional<chantier> chantierOptional = chantierepo.findById(ouvrierDTO.getId_chantier());
            chantierOptional.ifPresent(existingOuvrier::setChantier);
        }

        // Sauvegarde de l'entité mise à jour
        ouvrier updatedEntity = ouvrierepo.save(existingOuvrier);

        // Retourner le DTO mis à jour
        return convertToDTO(updatedEntity);
    }

    @Override
    public void deleteOuvrier(int id) {
        if (!ouvrierepo.existsById(id)) {
            throw new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + id);
        }
        ouvrierepo.deleteById(id);

    }

    @Override
    public List<OuvrierDTO> findByNom(String nom) {
        List<ouvrier> ouvriers = ouvrierepo.findByNomContainingIgnoreCase(nom);
        return ouvriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OuvrierDTO> findByCin(String cin) {
        return ouvrierepo.findByCin(cin)
                .map(this::convertToDTO);
    }

    @Override
    public List<OuvrierDTO> findByType(Type type) {
        List<ouvrier> ouvriers = ouvrierepo.findByType(type);
        return ouvriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OuvrierDTO> findByChantier(int chantierId) {
        List<ouvrier> ouvriers = ouvrierepo.findByChantier_Id(chantierId);
        return ouvriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCin(String cin) {
        return ouvrierepo.existsByCin(cin);
    }

    @Override
    public Map<String, Integer> getStatsByChantier(int chantierId) {
        List<OuvrierDTO> ouvriers = findByChantier(chantierId);

        Map<String, Integer> stats = new HashMap<>();
        for (OuvrierDTO ouvrier : ouvriers) {
            String type = ouvrier.getType().name(); // Assure-toi que type n'est pas null
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }

        return stats;
    }

}
