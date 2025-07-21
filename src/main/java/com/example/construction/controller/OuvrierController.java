package com.example.construction.controller;

import com.example.construction.DTO.OuvrierDTO;
import com.example.construction.entities.Type;
import com.example.construction.service.OuvrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            // Log des valeurs reçues
            System.out.println("------ Données reçues du client ------");
            System.out.println("prix_heure (brut): " + prixHeureStr);
            System.out.println("prix_jour (brut): " + prixJourStr);

            // Convertir les prix
            float prixHeure;
            float prixJour;

            try {
                prixHeure = Float.parseFloat(prixHeureStr.replace(",", "."));
                prixJour = Float.parseFloat(prixJourStr.replace(",", "."));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Les prix doivent être des nombres valides");
            }

            // Vérifier si le CIN existe déjà
            if (ouvrierService.existsByCin(cin)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Erreur : Ce CIN est déjà utilisé.");
            }

            // Traitement des fichiers photos - conversion en byte[]
            byte[] photoCINBytes = null;
            byte[] photoCNSSBytes = null;

            if (photoCINFile != null && !photoCINFile.isEmpty()) {
                try {
                    photoCINBytes = photoCINFile.getBytes();
                    System.out.println("Photo CIN uploadée, taille: " + photoCINBytes.length + " bytes");
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Erreur lors de la lecture du fichier CIN");
                }
            }

            if (photoCNSSFile != null && !photoCNSSFile.isEmpty()) {
                try {
                    photoCNSSBytes = photoCNSSFile.getBytes();
                    System.out.println("Photo CNSS uploadée, taille: " + photoCNSSBytes.length + " bytes");
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Erreur lors de la lecture du fichier CNSS");
                }
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
            dto.setPhotoCIN(photoCINBytes);
            dto.setPhotoCNSS(photoCNSSBytes);

            // Appeler le service
            OuvrierDTO created = ouvrierService.createOuvrier(dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // Endpoint pour récupérer les images
    @GetMapping("/images/cin/{id}")
    public ResponseEntity<byte[]> getCINImage(@PathVariable Integer id) {
        try {
            OuvrierDTO ouvrier = ouvrierService.findOuvrierById(id);
            if (ouvrier.getPhotoCIN() != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // ou MediaType.IMAGE_PNG selon vos besoins
                return ResponseEntity.ok().headers(headers).body(ouvrier.getPhotoCIN());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/images/cnss/{id}")
    public ResponseEntity<byte[]> getCNSSImage(@PathVariable Integer id) {
        try {
            OuvrierDTO ouvrier = ouvrierService.findOuvrierById(id);
            if (ouvrier.getPhotoCNSS() != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // ou MediaType.IMAGE_PNG selon vos besoins
                return ResponseEntity.ok().headers(headers).body(ouvrier.getPhotoCNSS());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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