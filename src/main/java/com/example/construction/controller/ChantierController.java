package com.example.construction.controller;

import com.example.construction.DTO.ChantierDTO;
import com.example.construction.entities.Etat;
import com.example.construction.service.ChantierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/chantiers")
@CrossOrigin("*")
public class ChantierController {

    private final ChantierService chantierService;

    @Autowired
    public ChantierController(ChantierService chantierService) {
        this.chantierService = chantierService;
    }

    @PostMapping
    public ResponseEntity<ChantierDTO> createChantier(@RequestBody ChantierDTO chantierDTO) {
        try {
            // Vérification de l'existence du numéro de police
            if (chantierService.existsByNumPolice(chantierDTO.getNumPolice())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            // Vérification de l'existence du titre foncier
            if (chantierService.existsByTitreFoncier(chantierDTO.getTitreFoncier())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            // Vérification de l'existence du nom de projet
            if (chantierService.existsByNomProjet(chantierDTO.getNomProjet())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            ChantierDTO createdChantier = chantierService.saveChantier(chantierDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChantier);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChantierDTO> getChantierById(@PathVariable int id) {
        try {
            ChantierDTO chantierDTO = chantierService.getChantierById(id);
            return ResponseEntity.ok(chantierDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ChantierDTO>> getAllChantiers() {
        try {
            List<ChantierDTO> chantiers = chantierService.getAllChantiers();
            return ResponseEntity.ok(chantiers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChantierDTO> updateChantier(@PathVariable int id, @RequestBody ChantierDTO chantierDTO) {
        try {
            System.out.println("Tentative de mise à jour du chantier avec ID: " + id);
            System.out.println("Données reçues: " + chantierDTO.toString());

            // Vérifier si le chantier existe
            ChantierDTO existingChantier = chantierService.getChantierById(id);
            System.out.println("Chantier existant trouvé: " + existingChantier.toString());

            // Vérifier si le numéro de police mis à jour existe déjà (sauf pour ce chantier)
            if (existingChantier.getNumPolice() != chantierDTO.getNumPolice()) {
                // Vérifier si ce numéro de police existe déjà pour un autre chantier
                if (chantierService.existsByNumPoliceAndIdNot(chantierDTO.getNumPolice(), id)) {
                    System.out.println("Conflit détecté - Numéro de police existe déjà: " + chantierDTO.getNumPolice());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            // Vérifier si le titre foncier mis à jour existe déjà (sauf pour ce chantier)
            if (!existingChantier.getTitreFoncier().equals(chantierDTO.getTitreFoncier())) {
                // Vérifier si ce titre foncier existe déjà pour un autre chantier
                if (chantierService.existsByTitreFoncierAndIdNot(chantierDTO.getTitreFoncier(), id)) {
                    System.out.println("Conflit détecté - Titre foncier existe déjà: " + chantierDTO.getTitreFoncier());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            // Vérifier si le nom de projet mis à jour existe déjà (sauf pour ce chantier)
            if (!existingChantier.getNomProjet().equals(chantierDTO.getNomProjet())) {
                // Vérifier si ce nom de projet existe déjà pour un autre chantier
                if (chantierService.existsByNomProjetAndIdNot(chantierDTO.getNomProjet(), id)) {
                    System.out.println("Conflit détecté - Nom de projet existe déjà: " + chantierDTO.getNomProjet());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            ChantierDTO updatedChantier = chantierService.updateChantier(id, chantierDTO);
            System.out.println("Chantier mis à jour avec succès: " + updatedChantier.toString());
            return ResponseEntity.ok(updatedChantier);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la mise à jour du chantier: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChantier(@PathVariable int id) {
        try {
            chantierService.deleteChantier(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-nom-projet/{nomProjet}")
    public ResponseEntity<ChantierDTO> getChantierByNomProjet(@PathVariable String nomProjet) {
        try {
            ChantierDTO chantierDTO = chantierService.findByNomProjet(nomProjet);
            return ResponseEntity.ok(chantierDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-titre-foncier/{titreFoncier}")
    public ResponseEntity<ChantierDTO> getChantierByTitreFoncier(@PathVariable String titreFoncier) {
        try {
            ChantierDTO chantierDTO = chantierService.findByTitreFoncier(titreFoncier);
            return ResponseEntity.ok(chantierDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-num-police/{numPolice}")
    public ResponseEntity<ChantierDTO> getChantierByNumPolice(@PathVariable int numPolice) {
        try {
            ChantierDTO chantierDTO = chantierService.findByNumPolice(numPolice);
            return ResponseEntity.ok(chantierDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-etat/{etat}")
    public ResponseEntity<List<ChantierDTO>> getChantiersByEtat(@PathVariable Etat etat) {
        try {
            List<ChantierDTO> chantiers = chantierService.findByEtat(etat);
            return ResponseEntity.ok(chantiers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<List<ChantierDTO>> getChantiersByClientId(@PathVariable int clientId) {
        try {
            List<ChantierDTO> chantiers = chantierService.findByClientId(clientId);
            return ResponseEntity.ok(chantiers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<ChantierDTO>> getChantiersByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fin) {
        try {
            List<ChantierDTO> chantiers = chantierService.findByDateOuvertureBetween(debut, fin);
            return ResponseEntity.ok(chantiers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/etat")
    public ResponseEntity<ChantierDTO> updateChantierEtat(@PathVariable int id, @RequestBody Etat etat) {
        try {
            ChantierDTO updatedChantier = chantierService.changerEtatChantier(id, etat);
            return ResponseEntity.ok(updatedChantier);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/check-num-police/{numPolice}")
    public ResponseEntity<Boolean> checkNumPoliceExists(@PathVariable int numPolice) {
        try {
            boolean exists = chantierService.existsByNumPolice(numPolice);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check-titre-foncier/{titreFoncier}")
    public ResponseEntity<Boolean> checkTitreFoncierExists(@PathVariable String titreFoncier) {
        try {
            boolean exists = chantierService.existsByTitreFoncier(titreFoncier);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}