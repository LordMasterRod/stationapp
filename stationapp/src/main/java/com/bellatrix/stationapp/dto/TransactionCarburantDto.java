package com.bellatrix.stationapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCarburantDto {
    private Long id;
    private LocalDateTime dateTransaction;
    private Double litresAchetes;
    private Double montantTotal;
    private Double pointsGagnes;
    private Double pointsUtilises;

    private ClientDto client;
    private StationServiceDto stationService;
    private UtilisateurDto utilisateur;

    // Constructeur pour mapper l'entité TransactionCarburant vers ce DTO
    public TransactionCarburantDto(com.bellatrix.stationapp.model.TransactionCarburant transaction) {
        this.id = transaction.getId();
        this.dateTransaction = transaction.getDateTransaction();
        this.litresAchetes = transaction.getLitresAchetes();
        this.montantTotal = transaction.getMontantTotal();
        this.pointsGagnes = transaction.getPointsGagnes();
        this.pointsUtilises = transaction.getPointsUtilises();

        // Assurez-vous que les objets liés sont initialisés avant de les mapper
        // grâce à l'annotation @Transactional dans le service.
        if (transaction.getClient() != null) {
            this.client = new ClientDto(transaction.getClient());
        }
        if (transaction.getStationService() != null) {
            this.stationService = new StationServiceDto(transaction.getStationService());
        }
        if (transaction.getUtilisateur() != null) {
            this.utilisateur = new UtilisateurDto(transaction.getUtilisateur());
        }
    }
}