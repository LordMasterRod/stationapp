package com.bellatrix.stationapp.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "regle_point")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglePoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "points_par_litre", nullable = false)
    private Double pointsParLitre;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "active", nullable = false)
    private Boolean active; // True si c'est la règle courante
}
