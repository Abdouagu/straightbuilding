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

    // Solution 1: Utiliser @Lob sans columnDefinition
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "photo_cin_data")
    private byte[] photoCINData;

    @Column(name = "photo_cin_name")
    private String photoCINName;

    @Column(name = "photo_cin_type")
    private String photoCINType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "photo_cnss_data")
    private byte[] photoCNSSData;

    @Column(name = "photo_cnss_name")
    private String photoCNSSName;

    @Column(name = "photo_cnss_type")
    private String photoCNSSType;

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