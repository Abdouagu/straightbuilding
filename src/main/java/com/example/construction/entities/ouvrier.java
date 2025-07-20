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
public class ouvrier {  // Notez le O majuscule, pour respecter les conventions Java

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
    private Date dateNaissance;  // Changer en camelCase pour correspondre au DTO

    @Column(name = "photo_CIN", nullable = false)
    private String photoCIN;     // Changer en camelCase

    @Column(name = "photo_CNSS", nullable = false)
    private String photoCNSS;    // Changer en camelCase

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "prix_heure", nullable = false)
    private float prixHeure;     // Changer en camelCase

    @Column(name = "prix_jour", nullable = false)
    private float prixJour;      // Changer en camelCase

    @ManyToOne
    @JoinColumn(name = "id_chantier", nullable = false)
    private chantier chantier;   // Notez le C majuscule

    @OneToMany(mappedBy = "ouvrier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pointage> pointages;
}
