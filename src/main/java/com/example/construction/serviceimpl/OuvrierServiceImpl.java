package com.example.construction.serviceimpl;

import com.example.construction.DTO.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.entities.chantier;
import com.example.construction.entities.ouvrier;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.service.OuvrierService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OuvrierServiceImpl implements OuvrierService {

    @Autowired
    private Ouvrierepo ouvrierepo;

    @Autowired
    private Chantierepo chantierepo;

    private OuvrierDTO convertToDTO(ouvrier ouvrier) {
        OuvrierDTO ouvrierDTO = new OuvrierDTO();
        ouvrierDTO.setId(ouvrier.getId());
        ouvrierDTO.setNom(ouvrier.getNom());
        ouvrierDTO.setPrenom(ouvrier.getPrenom());
        ouvrierDTO.setCin(ouvrier.getCin());
        ouvrierDTO.setDateNaissance(ouvrier.getDateNaissance());
        ouvrierDTO.setType(ouvrier.getType());
        ouvrierDTO.setPrixHeure(ouvrier.getPrixHeure());
        ouvrierDTO.setPrixJour(ouvrier.getPrixJour());

        // Pour les images, on génère des URLs pointant vers nos endpoints
        if (ouvrier.getPhotoCINData() != null) {
            ouvrierDTO.setPhotoCIN("/api/ouvriers/images/cin/" + ouvrier.getId());
            ouvrierDTO.setPhotoCINName(ouvrier.getPhotoCINName());
            ouvrierDTO.setPhotoCINType(ouvrier.getPhotoCINType());
        }

        if (ouvrier.getPhotoCNSSData() != null) {
            ouvrierDTO.setPhotoCNSS("/api/ouvriers/images/cnss/" + ouvrier.getId());
            ouvrierDTO.setPhotoCNSSName(ouvrier.getPhotoCNSSName());
            ouvrierDTO.setPhotoCNSSType(ouvrier.getPhotoCNSSType());
        }

        if (ouvrier.getChantier() != null) {
            ouvrierDTO.setId_chantier(ouvrier.getChantier().getId());
        }

        return ouvrierDTO;
    }

    private ouvrier convertToEntity(OuvrierDTO ouvrierDTO) {
        ouvrier entity = new ouvrier();
        entity.setId(ouvrierDTO.getId());
        entity.setNom(ouvrierDTO.getNom());
        entity.setPrenom(ouvrierDTO.getPrenom());
        entity.setCin(ouvrierDTO.getCin());
        entity.setDateNaissance(ouvrierDTO.getDateNaissance());
        entity.setType(ouvrierDTO.getType());
        entity.setPrixHeure(ouvrierDTO.getPrixHeure());
        entity.setPrixJour(ouvrierDTO.getPrixJour());

        // Métadonnées des images
        entity.setPhotoCINName(ouvrierDTO.getPhotoCINName());
        entity.setPhotoCINType(ouvrierDTO.getPhotoCINType());
        entity.setPhotoCNSSName(ouvrierDTO.getPhotoCNSSName());
        entity.setPhotoCNSSType(ouvrierDTO.getPhotoCNSSType());

        if (ouvrierDTO.getId_chantier() != null && ouvrierDTO.getId_chantier() > 0) {
            chantier chantier = chantierepo.findById(ouvrierDTO.getId_chantier())
                    .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec l'ID: " + ouvrierDTO.getId_chantier()));
            entity.setChantier(chantier);
        }

        return entity;
    }

    @Override
    public OuvrierDTO createOuvrier(OuvrierDTO ouvrierDTO) {
        ouvrier entity = convertToEntity(ouvrierDTO);
        return convertToDTO(ouvrierepo.save(entity));
    }

    @Override
    public OuvrierDTO createOuvrierWithFiles(OuvrierDTO ouvrierDTO, MultipartFile photoCIN, MultipartFile photoCNSS) {
        try {
            ouvrier entity = convertToEntity(ouvrierDTO);

            // Traitement de la photo CIN
            if (photoCIN != null && !photoCIN.isEmpty()) {
                entity.setPhotoCINData(photoCIN.getBytes());
                entity.setPhotoCINName(photoCIN.getOriginalFilename());
                entity.setPhotoCINType(photoCIN.getContentType());
                System.out.println("Photo CIN sauvegardée: " + photoCIN.getOriginalFilename() +
                        ", Taille: " + photoCIN.getBytes().length + " bytes");
            }

            // Traitement de la photo CNSS
            if (photoCNSS != null && !photoCNSS.isEmpty()) {
                entity.setPhotoCNSSData(photoCNSS.getBytes());
                entity.setPhotoCNSSName(photoCNSS.getOriginalFilename());
                entity.setPhotoCNSSType(photoCNSS.getContentType());
                System.out.println("Photo CNSS sauvegardée: " + photoCNSS.getOriginalFilename() +
                        ", Taille: " + photoCNSS.getBytes().length + " bytes");
            }

            ouvrier savedEntity = ouvrierepo.save(entity);
            return convertToDTO(savedEntity);

        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde avec fichiers: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la sauvegarde de l'ouvrier avec les fichiers", e);
        }
    }

    @Override
    public byte[] getPhotoCINData(int ouvrierId) {
        ouvrier entity = ouvrierepo.findById(ouvrierId)
                .orElseThrow(() -> new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + ouvrierId));
        return entity.getPhotoCINData();
    }

    @Override
    public byte[] getPhotoCNSSData(int ouvrierId) {
        ouvrier entity = ouvrierepo.findById(ouvrierId)
                .orElseThrow(() -> new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + ouvrierId));
        return entity.getPhotoCNSSData();
    }

    @Override
    public String getPhotoCINType(int ouvrierId) {
        ouvrier entity = ouvrierepo.findById(ouvrierId)
                .orElseThrow(() -> new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + ouvrierId));
        return entity.getPhotoCINType();
    }

    @Override
    public String getPhotoCNSSType(int ouvrierId) {
        ouvrier entity = ouvrierepo.findById(ouvrierId)
                .orElseThrow(() -> new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + ouvrierId));
        return entity.getPhotoCNSSType();
    }

    @Override
    public OuvrierDTO findOuvrierById(int id) {
        ouvrier entity = ouvrierepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + id));
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
                .orElseThrow(() -> new EntityNotFoundException("Ouvrier non trouvé avec l'ID: " + id));

        // Mise à jour des champs modifiés
        existingOuvrier.setNom(ouvrierDTO.getNom());
        existingOuvrier.setPrenom(ouvrierDTO.getPrenom());
        existingOuvrier.setCin(ouvrierDTO.getCin());
        existingOuvrier.setDateNaissance(ouvrierDTO.getDateNaissance());
        existingOuvrier.setType(ouvrierDTO.getType());
        existingOuvrier.setPrixHeure(ouvrierDTO.getPrixHeure());
        existingOuvrier.setPrixJour(ouvrierDTO.getPrixJour());

        // Mise à jour du chantier si nécessaire
        if (ouvrierDTO.getId_chantier() != null) {
            Optional<chantier> chantierOptional = chantierepo.findById(ouvrierDTO.getId_chantier());
            chantierOptional.ifPresent(existingOuvrier::setChantier);
        }

        // Note: Pour mettre à jour les images, il faudrait une méthode séparée avec MultipartFile

        ouvrier updatedEntity = ouvrierepo.save(existingOuvrier);
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
        List<ouvrier> ouvriers = ouvrierepo.findByChantier_Id(chantierId);

        Map<String, Integer> stats = new HashMap<>();

        // Compter les ouvriers par type
        for (ouvrier ouvrier : ouvriers) {
            String type = ouvrier.getType().toString().toLowerCase();
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }

        return stats;
    }
}