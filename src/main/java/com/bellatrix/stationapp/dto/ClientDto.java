package com.bellatrix.stationapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;
    private String numeroTelephone;
    private String nom;
    private String prenom;
    private LocalDateTime dateInscription;
    private Double soldePoints;

    // Constructeur pour mapper l'entité Client vers ce DTO
    public ClientDto(com.bellatrix.stationapp.model.Client client) {
        this.id = client.getId();
        this.numeroTelephone = client.getNumeroTelephone();
        this.nom = client.getNom();
        this.prenom = client.getPrenom();
        this.dateInscription = client.getDateInscription();
        this.soldePoints = client.getSoldePoints();
    }
}