package com.example.construction.serviceimpl;

import com.example.construction.dto.OuvrierDTO;
import com.example.construction.dto.PointageDTO;
import com.example.construction.dto.TableauPointageDTO;
import com.example.construction.entities.chantier;
import com.example.construction.entities.ouvrier;
import com.example.construction.entities.Pointage;
import com.example.construction.repository.Chantierepo;
import com.example.construction.repository.Ouvrierepo;
import com.example.construction.repository.PointageRepository;
import com.example.construction.service.OuvrierService;
import com.example.construction.service.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PointageServiceImpl implements PointageService {

    @Autowired
    private PointageRepository pointageRepository;

    @Autowired
    private Ouvrierepo ouvrierRepository;

    @Autowired
    private Chantierepo chantierRepository;

    @Autowired
    private OuvrierService ouvrierService;

    @Override
    public PointageDTO creerPointage(PointageDTO pointageDTO) {
        System.out.println("Création pointage: chantierId=" + pointageDTO.getChantierId() + ", ouvrierId=" + pointageDTO.getOuvrierId());
        Pointage pointage = mapToEntity(pointageDTO);
        Pointage savedPointage = pointageRepository.save(pointage);
        return mapToDTO(savedPointage);
    }

    @Override
    public PointageDTO modifierPointage(Long id, PointageDTO pointageDTO) {
        Pointage pointage = pointageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pointage non trouvé avec id: " + id));

        pointage.setPresent(pointageDTO.isPresent());
        pointage.setHeuresTravaillees(pointageDTO.getHeuresTravaillees());

        Pointage updatedPointage = pointageRepository.save(pointage);
        return mapToDTO(updatedPointage);
    }

    @Override
    public void supprimerPointage(Long id) {
        pointageRepository.deleteById(id);
    }

    @Override
    public PointageDTO getPointageById(Long id) {
        Pointage pointage = pointageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pointage non trouvé avec id: " + id));
        return mapToDTO(pointage);
    }

    @Override
    public List<PointageDTO> getPointagesByOuvrierId(int ouvrierId, LocalDate debut, LocalDate fin) {
        List<Pointage> pointages = pointageRepository.findByOuvrierIdAndDateBetween(ouvrierId, debut, fin);
        return pointages.stream().map(this::mapToDTO).toList();
    }

    @Override
    public TableauPointageDTO getTableauPointage(int chantierId, LocalDate dateDebut, LocalDate dateFin) {
        List<OuvrierDTO> ouvriers = ouvrierService.findByChantier(chantierId);
        List<Pointage> pointages = pointageRepository.findPointagesPourPeriode(chantierId, dateDebut, dateFin);

        Map<Integer, Map<LocalDate, Pointage>> pointagesParOuvrierEtDate = new HashMap<>();
        for (Pointage pointage : pointages) {
            Integer ouvrierId = pointage.getOuvrier().getId();
            LocalDate date = pointage.getDate();
            pointagesParOuvrierEtDate.computeIfAbsent(ouvrierId, k -> new HashMap<>()).put(date, pointage);
        }

        TableauPointageDTO tableau = new TableauPointageDTO();
        tableau.setChantierId(chantierId);
        tableau.setDateDebut(dateDebut);
        tableau.setDateFin(dateFin);

        List<TableauPointageDTO.LignePointageDTO> lignes = new ArrayList<>();

        for (OuvrierDTO ouvrier : ouvriers) {
            TableauPointageDTO.LignePointageDTO ligne = new TableauPointageDTO.LignePointageDTO();
            ligne.setOuvrier(ouvrier);
            ligne.setPrixHeure(ouvrier.getPrixHeure());
            ligne.setPrixJour(ouvrier.getPrixJour());

            List<TableauPointageDTO.JourPointageDTO> jours = new ArrayList<>();
            float totalAPayer = 0f;

            LocalDate currentDate = dateDebut;
            while (!currentDate.isAfter(dateFin)) {
                TableauPointageDTO.JourPointageDTO jour = new TableauPointageDTO.JourPointageDTO();
                jour.setDate(currentDate);

                Pointage pointage = pointagesParOuvrierEtDate
                        .getOrDefault(ouvrier.getId(), new HashMap<>())
                        .get(currentDate);

                if (pointage != null && pointage.isPresent()) {
                    jour.setPresent(true);
                    jour.setHeuresTravaillees(pointage.getHeuresTravaillees());
                    jour.setPointageId(pointage.getId());

                    float montant = calculerMontantJournalier(pointage.getHeuresTravaillees(), ouvrier.getPrixHeure(), ouvrier.getPrixJour());
                    jour.setMontantJournalier(montant);
                    totalAPayer += montant;
                } else {
                    jour.setPresent(false);
                    jour.setHeuresTravaillees(0f);
                    jour.setMontantJournalier(0f);
                }

                jours.add(jour);
                currentDate = currentDate.plusDays(1);
            }

            ligne.setJours(jours);
            ligne.setTotalAPayer(totalAPayer);
            lignes.add(ligne);
        }

        tableau.setLignesPointage(lignes);
        return tableau;
    }

    @Override
    public TableauPointageDTO sauvegarderTableauPointage(TableauPointageDTO tableauPointageDTO) {
        Integer chantierId = tableauPointageDTO.getChantierId();
        System.out.println("=== SAUVEGARDE TABLEAU - Chantier ID: " + chantierId + " ===");

        for (TableauPointageDTO.LignePointageDTO ligne : tableauPointageDTO.getLignesPointage()) {
            Integer ouvrierId = ligne.getOuvrier().getId();
            System.out.println("Traitement ouvrier ID: " + ouvrierId);

            for (TableauPointageDTO.JourPointageDTO jour : ligne.getJours()) {
                System.out.println("Traitement jour: " + jour.getDate() + ", présent: " + jour.isPresent());

                if (jour.isPresent()) {
                    PointageDTO pointageDTO = new PointageDTO();
                    pointageDTO.setOuvrierId(ouvrierId);
                    pointageDTO.setChantierId(chantierId);
                    pointageDTO.setDate(jour.getDate());
                    pointageDTO.setPresent(true);
                    pointageDTO.setHeuresTravaillees(jour.getHeuresTravaillees() != null ? jour.getHeuresTravaillees() : 9.0f);

                    System.out.println("PointageDTO créé pour sauvegarde: " + pointageDTO);

                    if (jour.getPointageId() != null) {
                        System.out.println("Modification pointage existant ID: " + jour.getPointageId());
                        modifierPointage(jour.getPointageId(), pointageDTO);
                    } else {
                        System.out.println("Création nouveau pointage");
                        PointageDTO nouveauPointage = creerPointage(pointageDTO);
                        jour.setPointageId(nouveauPointage.getId());
                        System.out.println("Nouveau pointage créé avec ID: " + nouveauPointage.getId());
                    }
                } else if (jour.getPointageId() != null) {
                    System.out.println("Suppression pointage ID: " + jour.getPointageId());
                    supprimerPointage(jour.getPointageId());
                    jour.setPointageId(null);
                }
            }
        }

        return getTableauPointage(chantierId, tableauPointageDTO.getDateDebut(), tableauPointageDTO.getDateFin());
    }

    @Override
    public float calculerTotalPaiement(int ouvrierId, LocalDate dateDebut, LocalDate dateFin) {
        List<Pointage> pointages = pointageRepository.findByOuvrierIdAndDateBetween(ouvrierId, dateDebut, dateFin);

        ouvrier ouvrier = ouvrierRepository.findById(ouvrierId)
                .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé avec id: " + ouvrierId));

        float totalPaiement = 0f;
        for (Pointage pointage : pointages) {
            if (pointage.isPresent() && pointage.getHeuresTravaillees() != null) {
                totalPaiement += calculerMontantJournalier(pointage.getHeuresTravaillees(), ouvrier.getPrixHeure(), ouvrier.getPrixJour());
            }
        }

        return totalPaiement;
    }

    private float calculerMontantJournalier(float heuresTravaillees, float prixHeure, float prixJour) {
        if (heuresTravaillees < 9.0f) {
            return heuresTravaillees * prixHeure;
        } else if (heuresTravaillees == 9.0f) {
            return prixJour;
        } else {
            return prixJour + ((heuresTravaillees - 9.0f) * prixHeure);
        }
    }

    // Dans PointageServiceImpl.java - Remplacer la méthode getTableauxExistants

    // Dans PointageServiceImpl.java - Remplacer la méthode getTableauxExistants

    @Override
    public List<TableauPointageDTO> getTableauxExistants(int chantierId) {
        // Récupérer tous les pointages pour ce chantier
        List<Pointage> tousLesPointages = pointageRepository.findByChantierIdOrderByDateAsc(chantierId);

        if (tousLesPointages.isEmpty()) {
            return new ArrayList<>();
        }

        // Grouper les pointages par périodes de 15 jours
        Map<String, TableauPointageDTO> periodesMap = new HashMap<>();

        for (Pointage pointage : tousLesPointages) {
            LocalDate datePointage = pointage.getDate();

            // Chercher si cette date appartient à une période existante
            String periodeKey = null;
            for (String key : periodesMap.keySet()) {
                TableauPointageDTO tableau = periodesMap.get(key);
                if (!datePointage.isBefore(tableau.getDateDebut()) && !datePointage.isAfter(tableau.getDateFin())) {
                    periodeKey = key;
                    break;
                }
            }

            // Si aucune période existante ne correspond, en créer une nouvelle
            if (periodeKey == null) {
                // Chercher la date de début la plus proche parmi les pointages existants
                LocalDate dateDebut = trouverDateDebutPeriode(datePointage, tousLesPointages);
                LocalDate dateFin = dateDebut.plusDays(14);

                periodeKey = dateDebut.toString();
                TableauPointageDTO nouveauTableau = new TableauPointageDTO();
                nouveauTableau.setChantierId(chantierId);
                nouveauTableau.setDateDebut(dateDebut);
                nouveauTableau.setDateFin(dateFin);

                periodesMap.put(periodeKey, nouveauTableau);
            }
        }

        // Convertir en liste et trier par date de début décroissante
        List<TableauPointageDTO> tableaux = new ArrayList<>(periodesMap.values());
        tableaux.sort((t1, t2) -> t2.getDateDebut().compareTo(t1.getDateDebut()));

        return tableaux;
    }

    // Nouvelle méthode helper pour trouver la date de début d'une période
    private LocalDate trouverDateDebutPeriode(LocalDate datePointage, List<Pointage> tousLesPointages) {
        // Chercher les pointages dans une fenêtre de 15 jours avant cette date
        LocalDate dateDebutPotentielle = datePointage.minusDays(14);

        // Trouver la date de début la plus ancienne dans cette fenêtre
        LocalDate dateDebutTrouvee = datePointage;

        for (Pointage p : tousLesPointages) {
            LocalDate dateP = p.getDate();
            if (!dateP.isBefore(dateDebutPotentielle) && !dateP.isAfter(datePointage)) {
                if (dateP.isBefore(dateDebutTrouvee)) {
                    dateDebutTrouvee = dateP;
                }
            }
        }

        return dateDebutTrouvee;
    }

    private Pointage mapToEntity(PointageDTO pointageDTO) {
        System.out.println("=== DEBUG mapToEntity ===");
        System.out.println("PointageDTO reçu: " + pointageDTO);
        System.out.println("OuvrierId: " + pointageDTO.getOuvrierId());
        System.out.println("ChantierId: " + pointageDTO.getChantierId());

        Pointage pointage = new Pointage();
        pointage.setId(pointageDTO.getId());
        pointage.setDate(pointageDTO.getDate());
        pointage.setPresent(pointageDTO.isPresent());
        pointage.setHeuresTravaillees(pointageDTO.getHeuresTravaillees());

        // Vérification avec Integer au lieu de int
        if (pointageDTO.getOuvrierId() == null || pointageDTO.getOuvrierId() == 0) {
            throw new RuntimeException("ID ouvrier manquant pour le pointage: " + pointageDTO.getOuvrierId());
        }
        if (pointageDTO.getChantierId() == null || pointageDTO.getChantierId() == 0) {
            throw new RuntimeException("ID chantier manquant pour le pointage: " + pointageDTO.getChantierId());
        }

        ouvrier ouvrier = ouvrierRepository.findById(pointageDTO.getOuvrierId())
                .orElseThrow(() -> new RuntimeException("Ouvrier non trouvé avec id: " + pointageDTO.getOuvrierId()));
        pointage.setOuvrier(ouvrier);
        System.out.println("Ouvrier trouvé: " + ouvrier.getId());

        chantier chantier = chantierRepository.findById(pointageDTO.getChantierId())
                .orElseThrow(() -> new RuntimeException("Chantier non trouvé avec id: " + pointageDTO.getChantierId()));
        pointage.setChantier(chantier);
        System.out.println("Chantier trouvé: " + chantier.getId());

        System.out.println("=== FIN DEBUG mapToEntity ===");
        return pointage;
    }

    private PointageDTO mapToDTO(Pointage pointage) {
        PointageDTO dto = new PointageDTO();
        dto.setId(pointage.getId());
        dto.setDate(pointage.getDate());
        dto.setPresent(pointage.isPresent());
        dto.setHeuresTravaillees(pointage.getHeuresTravaillees());

        if (pointage.getOuvrier() != null) {
            dto.setOuvrierId(pointage.getOuvrier().getId());
        }
        if (pointage.getChantier() != null) {
            dto.setChantierId(pointage.getChantier().getId());
        }

        return dto;
    }
}