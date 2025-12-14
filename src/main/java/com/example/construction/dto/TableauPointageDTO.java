package com.example.construction.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableauPointageDTO {
    private Integer chantierId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private List<LignePointageDTO> lignesPointage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LignePointageDTO {
        private OuvrierDTO ouvrier;
        private List<JourPointageDTO> jours;
        private float prixHeure;
        private float prixJour;
        private float totalAPayer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JourPointageDTO {
        private LocalDate date;
        private boolean present;
        private Float heuresTravaillees; // Nombre d'heures travaillées (null si non présent)
        private Long pointageId; // Pour la mise à jour
        private float montantJournalier;// Montant calculé pour ce jour
        private Integer chantierId;
        private Integer ouvrierId;
    }
}
