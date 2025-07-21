package com.example.construction.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="ouvrier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ouvrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique=true)
    private String cin;

    @Column(name = "date_naissance", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    // Stockage des photos en BYTEA dans PostgreSQL
    @Lob
    @Column(name = "photo_cin", columnDefinition = "BYTEA")
    private byte[] photoCIN;

    @Lob
    @Column(name = "photo_cnss", columnDefinition = "BYTEA")
    private byte[] photoCNSS;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "prix_heure", nullable = false)
    private float prixHeure;

    @Column(name = "prix_jour", nullable = false)
    private float prixJour;

    @ManyToOne
    @JoinColumn(name = "id_chantier", nullable = false)
    private chantier chantier;

    @OneToMany(mappedBy = "ouvrier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pointage> pointages;
}