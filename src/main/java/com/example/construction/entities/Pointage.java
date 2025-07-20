package com.example.construction.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pointage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ouvrier_id", nullable = false)
    private ouvrier ouvrier;

    @ManyToOne
    @JoinColumn(name = "chantier_id", nullable = false)
    private chantier chantier;

    private LocalDate date;

    private boolean present;


    private Float heuresTravaillees;
}
