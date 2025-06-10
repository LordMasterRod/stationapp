package com.bellatrix.stationapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_carburant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCarburant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_service_id", nullable = false)
    private StationService stationService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_transaction", nullable = false)
    private LocalDateTime dateTransaction;

    @Column(name = "litres_achetes", nullable = false)
    private Double litresAchetes;

    @Column(name = "montant_total", nullable = false)
    private Double montantTotal;

    @Column(name = "points_gagnes", nullable = false)
    private Double pointsGagnes;

    @Column(name = "points_utilises", nullable = false)
    private Double pointsUtilises = 0.0;
}