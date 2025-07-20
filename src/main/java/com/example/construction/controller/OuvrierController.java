package com.example.construction.controller;

import com.example.construction.DTO.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.service.OuvrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ouvriers")
@CrossOrigin("*")
public class OuvrierController {

    private final OuvrierService ouvrierService;

    @Autowired
    public OuvrierController(OuvrierService ouvrierService) {
        this.ouvrierService = ouvrierService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createOuvrier(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("cin") String cin,
            @RequestParam("date_naissance") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateNaissance,
            @RequestParam("type") Type type,
            @RequestParam("prix_heure") String prixHeureStr,
            @RequestParam("prix_jour") String prixJourStr,
            @RequestParam("id_chantier") Integer idChantier,
            @RequestParam(value = "photo_CIN", required = false) MultipartFile photoCINFile,
            @RequestParam(value = "photo_CNSS", required = false) MultipartFile photoCNSSFile
    ) {
        try {
            // Log des valeurs reçues avant conversion
            System.out.println("------ Données reçues du client ------");
            System.out.println("prix_heure (brut): " + prixHeureStr);
            System.out.println("prix_jour (brut): " + prixJourStr);

            // Convertir les prix de String vers float
            float prixHeure;
            float prixJour;

            try {
                // Remplacer les virgules par des points si nécessaire
                prixHeure = Float.parseFloat(prixHeureStr.replace(",", "."));
                prixJour = Float.parseFloat(prixJourStr.replace(",", "."));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Les prix doivent être des nombres valides");
            }

            // Log après conversion
            System.out.println("prix_heure (converti): " + prixHeure);
            System.out.println("prix_jour (converti): " + prixJour);

            // Vérifier si le CIN existe déjà
            if (ouvrierService.existsByCin(cin)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Erreur : Ce CIN est déjà utilisé.");
            }

            // Traitement des fichiers photos
            String photoCINDirectory = "C:/MesPhotos/cin/";
            String photoCNSSDirectory = "C:/MesPhotos/cnss/";
            new File(photoCINDirectory).mkdirs();
            new File(photoCNSSDirectory).mkdirs();

            String photoCINPath = null;
            String photoCNSSPath = null;

            // Méthode à mettre à jour dans votre contrôleur

            if (photoCINFile != null && !photoCINFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + sanitizeFileName(photoCINFile.getOriginalFilename());
                File dest = new File(photoCINDirectory + fileName);
                photoCINFile.transferTo(dest);
                // Stocker uniquement le nom du fichier, pas le chemin complet
                photoCINPath = fileName;  // Modification ici
            }

            if (photoCNSSFile != null && !photoCNSSFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + sanitizeFileName(photoCNSSFile.getOriginalFilename());
                File dest = new File(photoCNSSDirectory + fileName);
                photoCNSSFile.transferTo(dest);
                // Stocker uniquement le nom du fichier, pas le chemin complet
                photoCNSSPath = fileName;  // Modification ici
            }

            // Créer le DTO
            OuvrierDTO dto = new OuvrierDTO();
            dto.setNom(nom);
            dto.setPrenom(prenom);
            dto.setCin(cin);
            dto.setDateNaissance(dateNaissance);
            dto.setType(type);
            dto.setPrixHeure(prixHeure);
            dto.setPrixJour(prixJour);
            dto.setId_chantier(idChantier);
            dto.setPhotoCIN(photoCINPath);
            dto.setPhotoCNSS(photoCNSSPath);

            // Log avant d'envoyer au service
            System.out.println("Contrôleur - prixHeure avant service: " + dto.getPrixHeure());
            System.out.println("Contrôleur - prixJour avant service: " + dto.getPrixJour());
            System.out.println("Contrôleur - photoCIN path: " + dto.getPhotoCIN());
            System.out.println("Contrôleur - photoCNSS path: " + dto.getPhotoCNSS());

            // Appeler le service
            OuvrierDTO created = ouvrierService.createOuvrier(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    @GetMapping("/{id}")
    public ResponseEntity<OuvrierDTO> getOuvrierById(@PathVariable int id) {
        try {
            OuvrierDTO ouvrierDTO = ouvrierService.findOuvrierById(id);
            return ResponseEntity.ok(ouvrierDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OuvrierDTO>> getAllOuvriers() {
        List<OuvrierDTO> ouvriers = ouvrierService.getAllOuvriers();
        return ResponseEntity.ok(ouvriers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OuvrierDTO> updateOuvrier(@PathVariable int id, @RequestBody OuvrierDTO ouvrierDTO) {
        try {
            OuvrierDTO existingOuvrier = ouvrierService.findOuvrierById(id);
            if (!existingOuvrier.getCin().equals(ouvrierDTO.getCin()) &&
                    ouvrierService.existsByCin(ouvrierDTO.getCin())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            OuvrierDTO updatedOuvrier = ouvrierService.updateOuvrier(id, ouvrierDTO);
            return ResponseEntity.ok(updatedOuvrier);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOuvrier(@PathVariable int id) {
        try {
            ouvrierService.deleteOuvrier(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-nom/{nom}")
    public ResponseEntity<List<OuvrierDTO>> getOuvriersByNom(@PathVariable String nom) {
        List<OuvrierDTO> ouvriers = ouvrierService.findByNom(nom);
        return ResponseEntity.ok(ouvriers);
    }

    @GetMapping("/by-cin/{cin}")
    public ResponseEntity<OuvrierDTO> getOuvrierByCin(@PathVariable String cin) {
        return ouvrierService.findByCin(cin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<OuvrierDTO>> getOuvriersByType(@PathVariable Type type) {
        List<OuvrierDTO> ouvriers = ouvrierService.findByType(type);
        return ResponseEntity.ok(ouvriers);
    }

    @GetMapping("/by-chantier/{chantierId}")
    public ResponseEntity<List<OuvrierDTO>> getOuvriersByChantier(@PathVariable int chantierId) {
        List<OuvrierDTO> ouvriers = ouvrierService.findByChantier(chantierId);
        return ResponseEntity.ok(ouvriers);
    }

    @GetMapping("/check-cin/{cin}")
    public ResponseEntity<Boolean> checkCinExists(@PathVariable String cin) {
        boolean exists = ouvrierService.existsByCin(cin);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/stats/by-chantier/{chantierId}")
    public ResponseEntity<Map<String, Integer>> getOuvrierStatsByChantier(@PathVariable int chantierId) {
        Map<String, Integer> stats = ouvrierService.getStatsByChantier(chantierId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/types")
    public ResponseEntity<Type[]> getTypes() {
        return ResponseEntity.ok(Type.values());
    }
}
