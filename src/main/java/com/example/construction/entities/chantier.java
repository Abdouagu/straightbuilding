package com.example.construction.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "chantier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class chantier { // avec une majuscule
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nomProjet;

    @Column(nullable = false, unique = true)
    private String titreFoncier;

    @Column(nullable = false, unique = true)
    private int numPolice;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOuverture;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateContrat; // <-- camelCase conseillé

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private int duree;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Etat etat;

    @Column(nullable = false)
    private float budget;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private client client;

    @OneToMany(mappedBy = "chantier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ouvrier> ouvriers;

    @OneToMany(mappedBy = "chantier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pointage> pointages;
}

