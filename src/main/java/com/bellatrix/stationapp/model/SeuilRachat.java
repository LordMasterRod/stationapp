package com.bellatrix.stationapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "seuil_rachat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeuilRachat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "points_requis", nullable = false)
    private Double pointsRequis;

    @Column(name = "valeur_monetaire", nullable = false)
    private Double valeurMonetaire;

    @Column(name = "description", length = 255)
    private String description;
}
