package com.example.construction.controller;

import com.example.construction.dto.PointageDTO;
import com.example.construction.dto.TableauPointageDTO;
import com.example.construction.entities.Pointage;
import com.example.construction.repository.PointageRepository;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.service.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pointages")
@CrossOrigin(origins = "*")
public class PointageController {

    @Autowired
    private PointageService pointageService;

    @Autowired
    private PointageRepository pointageRepository;

    @Autowired
    private Chantierepo chantierRepository;

    @Autowired
    private Ouvrierepo ouvrierRepository;

    @PostMapping
    public ResponseEntity<PointageDTO> creerPointage(@RequestBody PointageDTO pointageDTO) {
        try {
            PointageDTO nouveauPointage = pointageService.creerPointage(pointageDTO);
            return new ResponseEntity<>(nouveauPointage, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du pointage: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointageDTO> modifierPointage(@PathVariable Long id, @RequestBody PointageDTO pointageDTO) {
        try {
            PointageDTO pointageModifie = pointageService.modifierPointage(id, pointageDTO);
            return ResponseEntity.ok(pointageModifie);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification du pointage: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPointage(@PathVariable Long id) {
        try {
            pointageService.supprimerPointage(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du pointage: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointageDTO> getPointageById(@PathVariable Long id) {
        try {
            PointageDTO pointageDTO = pointageService.getPointageById(id);
            return ResponseEntity.ok(pointageDTO);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du pointage: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/ouvrier/{ouvrierId}")
    public ResponseEntity<List<PointageDTO>> getPointagesByOuvrierId(
            @PathVariable Integer ouvrierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            List<PointageDTO> pointages = pointageService.getPointagesByOuvrierId(ouvrierId, debut, fin);
            return ResponseEntity.ok(pointages);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des pointages par ouvrier: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tableau")
    public ResponseEntity<TableauPointageDTO> getTableauPointage(
            @RequestParam Integer chantierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            System.out.println("Récupération tableau pointage - Chantier: " + chantierId +
                    ", Début: " + dateDebut + ", Fin: " + dateFin);

            TableauPointageDTO tableau = pointageService.getTableauPointage(chantierId, dateDebut, dateFin);
            return ResponseEntity.ok(tableau);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du tableau de pointage: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/tableau")
    public ResponseEntity<?> sauvegarderTableauPointage(@RequestBody TableauPointageDTO tableauPointageDTO) {
        try {
            System.out.println("Sauvegarde tableau pointage reçu:");
            System.out.println("Chantier ID: " + tableauPointageDTO.getChantierId());
            System.out.println("Date début: " + tableauPointageDTO.getDateDebut());
            System.out.println("Date fin: " + tableauPointageDTO.getDateFin());
            System.out.println("Nombre de lignes: " +
                    (tableauPointageDTO.getLignesPointage() != null ?
                            tableauPointageDTO.getLignesPointage().size() : "null"));

            // Validation basique
            if (tableauPointageDTO.getChantierId() == null) {
                return ResponseEntity.badRequest().body("ID du chantier manquant");
            }
            if (tableauPointageDTO.getDateDebut() == null) {
                return ResponseEntity.badRequest().body("Date de début manquante");
            }
            if (tableauPointageDTO.getDateFin() == null) {
                return ResponseEntity.badRequest().body("Date de fin manquante");
            }
            if (tableauPointageDTO.getLignesPointage() == null || tableauPointageDTO.getLignesPointage().isEmpty()) {
                return ResponseEntity.badRequest().body("Aucune ligne de pointage fournie");
            }

            TableauPointageDTO tableauMisAJour = pointageService.sauvegarderTableauPointage(tableauPointageDTO);
            return ResponseEntity.ok(tableauMisAJour);

        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du tableau de pointage: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne: " + e.getMessage());
        }
    }

    @GetMapping("/paiement/{ouvrierId}")
    public ResponseEntity<Float> calculerTotalPaiement(
            @PathVariable Integer ouvrierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        try {
            float totalPaiement = pointageService.calculerTotalPaiement(ouvrierId, dateDebut, dateFin);
            return ResponseEntity.ok(totalPaiement);
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du total de paiement: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/jour/{pointageId}")
    public ResponseEntity<PointageDTO> modifierHeuresTravaillees(
            @PathVariable Long pointageId,
            @RequestParam Float heuresTravaillees) {
        try {
            PointageDTO pointageDTO = pointageService.getPointageById(pointageId);
            pointageDTO.setHeuresTravaillees(heuresTravaillees);

            PointageDTO pointageModifie = pointageService.modifierPointage(pointageId, pointageDTO);
            return ResponseEntity.ok(pointageModifie);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification des heures travaillées: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/jour/toggle")
    public ResponseEntity<PointageDTO> togglePresence(
            @RequestParam Integer ouvrierId,
            @RequestParam Integer chantierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Float heuresTravaillees) {
        try {
            Optional<Pointage> optionalPointage = pointageRepository.findByOuvrierIdAndChantierIdAndDate(
                    ouvrierId, chantierId, date);

            if (optionalPointage.isPresent()) {
                Pointage pointage = optionalPointage.get();
                boolean nouveauStatut = !pointage.isPresent();
                Float nouvellesHeures = nouveauStatut ? (heuresTravaillees != null ? heuresTravaillees : 9.0f) : 0f;

                PointageDTO pointageDTO = new PointageDTO();
                pointageDTO.setId(pointage.getId());
                pointageDTO.setOuvrierId(ouvrierId);
                pointageDTO.setChantierId(chantierId);
                pointageDTO.setDate(date);
                pointageDTO.setPresent(nouveauStatut);
                pointageDTO.setHeuresTravaillees(nouvellesHeures);

                return ResponseEntity.ok(pointageService.modifierPointage(pointage.getId(), pointageDTO));
            }

            // Sinon, créer un nouveau pointage avec présence cochée
            PointageDTO pointageDTO = new PointageDTO();
            pointageDTO.setOuvrierId(ouvrierId);
            pointageDTO.setChantierId(chantierId);
            pointageDTO.setDate(date);
            pointageDTO.setPresent(true);
            pointageDTO.setHeuresTravaillees(heuresTravaillees != null ? heuresTravaillees : 9.0f);

            return new ResponseEntity<>(pointageService.creerPointage(pointageDTO), HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Erreur lors du toggle de présence: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tableaux/{chantierId}")
    public ResponseEntity<List<TableauPointageDTO>> getTableauxExistants(@PathVariable Integer chantierId) {
        try {
            System.out.println("Récupération des tableaux existants pour le chantier: " + chantierId);

            List<TableauPointageDTO> tableaux = pointageService.getTableauxExistants(chantierId);
            return ResponseEntity.ok(tableaux);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des tableaux existants: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}