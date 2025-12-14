package com.example.construction.serviceimpl;

import com.example.construction.dto.ChantierDTO;
import com.example.construction.entities.Etat;
import com.example.construction.entities.chantier;
import com.example.construction.entities.client;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Clientrepo;
import com.example.construction.service.ChantierService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChantierServiceImpl implements ChantierService {

    @Autowired
    private Chantierepo chantierRepository;

    @Autowired
    private Clientrepo clientRepository;

    // Méthode pour convertir l'entité en dto
    private ChantierDTO convertToDTO(chantier chantier) {
        ChantierDTO dto = new ChantierDTO();
        dto.setId(chantier.getId());
        dto.setNomProjet(chantier.getNomProjet());
        dto.setTitreFoncier(chantier.getTitreFoncier());
        dto.setNumPolice(chantier.getNumPolice());
        dto.setDateOuverture(chantier.getDateOuverture());
        dto.setDateContrat(chantier.getDateContrat());
        dto.setAdresse(chantier.getAdresse());
        dto.setDuree(chantier.getDuree());
        dto.setEtat(chantier.getEtat());
        dto.setBudget(chantier.getBudget());

        if (chantier.getClient() != null) {
            dto.setId_client(chantier.getClient().getId());
        }

        return dto;
    }

    // Méthode pour convertir le dto en entité
    private chantier convertToEntity(ChantierDTO chantierDTO) {
        chantier entity = new chantier();
        entity.setId(chantierDTO.getId());
        entity.setNomProjet(chantierDTO.getNomProjet());
        entity.setTitreFoncier(chantierDTO.getTitreFoncier());
        entity.setNumPolice(chantierDTO.getNumPolice());
        entity.setDateOuverture(chantierDTO.getDateOuverture());
        entity.setDateContrat(chantierDTO.getDateContrat());
        entity.setAdresse(chantierDTO.getAdresse());
        entity.setDuree(chantierDTO.getDuree());
        entity.setEtat(chantierDTO.getEtat());
        entity.setBudget(chantierDTO.getBudget());

        // Récupérer le client par son ID
        if (chantierDTO.getId_client() > 0) {
            client client = clientRepository.findById(chantierDTO.getId_client())
                    .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + chantierDTO.getId_client()));
            entity.setClient(client);
        }

        return entity;
    }

    @Override
    public ChantierDTO saveChantier(ChantierDTO chantierDTO) {
        chantier entity = convertToEntity(chantierDTO);
        chantier savedEntity = chantierRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public ChantierDTO getChantierById(int id) {
        chantier entity = chantierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec l'ID: " + id));
        return convertToDTO(entity);
    }

    @Override
    public List<ChantierDTO> getAllChantiers() {
        return chantierRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ChantierDTO updateChantier(int id, ChantierDTO chantierDTO) {
        // Vérifier si le chantier existe
        chantier existingChantier = chantierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec l'ID: " + id));

        // Mise à jour des champs modifiés
        existingChantier.setNomProjet(chantierDTO.getNomProjet());
        existingChantier.setTitreFoncier(chantierDTO.getTitreFoncier());
        existingChantier.setNumPolice(chantierDTO.getNumPolice());
        existingChantier.setDateOuverture(chantierDTO.getDateOuverture());
        existingChantier.setDateContrat(chantierDTO.getDateContrat());
        existingChantier.setAdresse(chantierDTO.getAdresse());
        existingChantier.setDuree(chantierDTO.getDuree());
        existingChantier.setEtat(chantierDTO.getEtat());
        existingChantier.setBudget(chantierDTO.getBudget());

        // Mise à jour du client si nécessaire
        if (chantierDTO.getId_client() != null && chantierDTO.getId_client() > 0) {
            client client = clientRepository.findById(chantierDTO.getId_client())
                    .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID: " + chantierDTO.getId_client()));
            existingChantier.setClient(client);
        }

        // Sauvegarde de l'entité mise à jour
        chantier updatedEntity = chantierRepository.save(existingChantier);

        // Retourner le dto mis à jour avec l'ID
        ChantierDTO result = convertToDTO(updatedEntity);

        // S'assurer que l'ID est bien défini
        if (result.getId() == null) {
            result.setId(updatedEntity.getId());
        }

        return result;
    }


    @Override
    public void deleteChantier(int id) {
        if (!chantierRepository.existsById(id)) {
            throw new EntityNotFoundException("Chantier non trouvé avec l'ID: " + id);
        }
        chantierRepository.deleteById(id);
    }

    @Override
    public ChantierDTO findByNomProjet(String nomProjet) {
        chantier entity = chantierRepository.findByNomProjet(nomProjet)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec le nom de projet: " + nomProjet));
        return convertToDTO(entity);
    }

    @Override
    public ChantierDTO findByTitreFoncier(String titreFoncier) {
        chantier entity = chantierRepository.findByTitreFoncier(titreFoncier)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec le titre foncier: " + titreFoncier));
        return convertToDTO(entity);
    }

    @Override
    public ChantierDTO findByNumPolice(int numPolice) {
        chantier entity = chantierRepository.findByNumPolice(numPolice)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec le numéro de police: " + numPolice));
        return convertToDTO(entity);
    }

    @Override
    public List<ChantierDTO> findByEtat(Etat etat) {
        return chantierRepository.findByEtat(etat).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChantierDTO> findByClientId(int clientId) {
        return chantierRepository.findByClientId(clientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChantierDTO> findByDateOuvertureBetween(Date debut, Date fin) {
        return chantierRepository.findByDateOuvertureBetween(debut, fin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public ChantierDTO changerEtatChantier(int id, Etat nouvelEtat) {
        chantier entity = chantierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chantier non trouvé avec l'ID: " + id));

        entity.setEtat(nouvelEtat);
        chantier updatedEntity = chantierRepository.save(entity);
        return convertToDTO(updatedEntity);
    }

    @Override
    public boolean existsByNumPolice(int numPolice) {
        return chantierRepository.existsByNumPolice(numPolice);
    }

    @Override
    public boolean existsByTitreFoncier(String titreFoncier) {
        return chantierRepository.existsByTitreFoncier(titreFoncier);
    }

    @Override
    public boolean existsByNomProjet(String nomProjet){
        return chantierRepository.existsByNomProjet(nomProjet);
    }

    // Méthodes à ajouter dans votre ChantierService

    @Override
    public boolean existsByNumPoliceAndIdNot(int numPolice, int id) {
        return chantierRepository.existsByNumPoliceAndIdNot(numPolice, id);
    }

    @Override
    public boolean existsByTitreFoncierAndIdNot(String titreFoncier, int id) {
        return chantierRepository.existsByTitreFoncierAndIdNot(titreFoncier, id);
    }

    @Override
    public boolean existsByNomProjetAndIdNot(String nomProjet, int id) {
        return chantierRepository.existsByNomProjetAndIdNot(nomProjet, id);
    }


}