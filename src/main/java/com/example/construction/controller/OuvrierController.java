package com.example.construction.controller;

import com.example.construction.dto.OuvrierDTO;
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

            // Créer le dto
            OuvrierDTO dto = new OuvrierDTO();
            dto.setNom(nom);
            dto.setPrenom(prenom);
            dto.setCin(cin);
            dto.setDateNaissance(dateNaissance);
            dto.setType(type);
            dto.setPrixHeure(prixHeure);
            dto.setPrixJour(prixJour);
            dto.setId_chantier(idChantier);

            // Traitement des photos - stockage en base
            if (photoCINFile != null && !photoCINFile.isEmpty()) {
                dto.setPhotoCINName(photoCINFile.getOriginalFilename());
                dto.setPhotoCINType(photoCINFile.getContentType());
            }

            if (photoCNSSFile != null && !photoCNSSFile.isEmpty()) {
                dto.setPhotoCNSSName(photoCNSSFile.getOriginalFilename());
                dto.setPhotoCNSSType(photoCNSSFile.getContentType());
            }

            System.out.println("Contrôleur - prixHeure avant service: " + dto.getPrixHeure());
            System.out.println("Contrôleur - prixJour avant service: " + dto.getPrixJour());

            // Appeler le service avec les fichiers
            OuvrierDTO created = ouvrierService.createOuvrierWithFiles(dto, photoCINFile, photoCNSSFile);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // NOUVEAU : Endpoint pour modifier un ouvrier avec fichiers
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateOuvrierWithFiles(
            @PathVariable int id,
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
            System.out.println("------ Modification ouvrier ID: " + id + " ------");
            System.out.println("CIN reçu: " + cin);
            System.out.println("prix_heure (brut): " + prixHeureStr);
            System.out.println("prix_jour (brut): " + prixJourStr);

            // Récupérer l'ouvrier existant
            OuvrierDTO existingOuvrier;
            try {
                existingOuvrier = ouvrierService.findOuvrierById(id);
            } catch (RuntimeException e) {
                System.err.println("Ouvrier non trouvé avec l'ID: " + id);
                return ResponseEntity.notFound().build();
            }

            // Convertir les prix
            float prixHeure;
            float prixJour;

            try {
                prixHeure = Float.parseFloat(prixHeureStr.replace(",", "."));
                prixJour = Float.parseFloat(prixJourStr.replace(",", "."));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Les prix doivent être des nombres valides");
            }

            // Vérifier si le CIN existe déjà (seulement si le CIN a changé)
            if (!existingOuvrier.getCin().equals(cin)) {
                System.out.println("CIN a changé de '" + existingOuvrier.getCin() + "' vers '" + cin + "'");
                if (ouvrierService.existsByCin(cin)) {
                    System.err.println("Conflit: Ce CIN est déjà utilisé par un autre ouvrier");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Erreur : Ce CIN est déjà utilisé par un autre ouvrier.");
                }
            } else {
                System.out.println("CIN inchangé: " + cin);
            }

            // Créer le dto avec les nouvelles données
            OuvrierDTO dto = new OuvrierDTO();
            dto.setId(id);
            dto.setNom(nom);
            dto.setPrenom(prenom);
            dto.setCin(cin);
            dto.setDateNaissance(dateNaissance);
            dto.setType(type);
            dto.setPrixHeure(prixHeure);
            dto.setPrixJour(prixJour);
            dto.setId_chantier(idChantier);

            // Traitement des photos
            if (photoCINFile != null && !photoCINFile.isEmpty()) {
                dto.setPhotoCINName(photoCINFile.getOriginalFilename());
                dto.setPhotoCINType(photoCINFile.getContentType());
                System.out.println("Nouvelle photo CIN reçue: " + photoCINFile.getOriginalFilename());
            }

            if (photoCNSSFile != null && !photoCNSSFile.isEmpty()) {
                dto.setPhotoCNSSName(photoCNSSFile.getOriginalFilename());
                dto.setPhotoCNSSType(photoCNSSFile.getContentType());
                System.out.println("Nouvelle photo CNSS reçue: " + photoCNSSFile.getOriginalFilename());
            }

            // Appeler le service pour la mise à jour avec fichiers
            OuvrierDTO updated = ouvrierService.updateOuvrierWithFiles(id, dto, photoCINFile, photoCNSSFile);
            System.out.println("Ouvrier modifié avec succès - ID: " + updated.getId());

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            System.err.println("Erreur RuntimeException: " + e.getMessage());
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la modification : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur générale: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // Nouveau endpoint pour récupérer les images
    @GetMapping("/images/cin/{ouvrierId}")
    public ResponseEntity<byte[]> getPhotoCIN(@PathVariable int ouvrierId) {
        try {
            byte[] imageData = ouvrierService.getPhotoCINData(ouvrierId);
            String contentType = ouvrierService.getPhotoCINType(ouvrierId);

            if (imageData == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            } else {
                headers.setContentType(MediaType.IMAGE_JPEG); // par défaut
            }

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/cnss/{ouvrierId}")
    public ResponseEntity<byte[]> getPhotoCNSS(@PathVariable int ouvrierId) {
        try {
            byte[] imageData = ouvrierService.getPhotoCNSSData(ouvrierId);
            String contentType = ouvrierService.getPhotoCNSSType(ouvrierId);

            if (imageData == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            } else {
                headers.setContentType(MediaType.IMAGE_JPEG); // par défaut
            }

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    public ResponseEntity<?> getAllOuvriers() {
        try {
            List<OuvrierDTO> ouvriers = ouvrierService.getAllOuvriers();
            return ResponseEntity.ok(ouvriers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur : " + e.getMessage());
        }
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