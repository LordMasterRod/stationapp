package com.bellatrix.stationapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {
    private Long id;
    private String username;
    private String role;
    private Long stationServiceId; // Inclure l'ID de la station-service si nécessaire

    // Constructeur pour mapper l'entité Utilisateur vers ce DTO
    public UtilisateurDto(com.bellatrix.stationapp.model.Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.username = utilisateur.getUsername();
        this.role = String.valueOf(utilisateur.getRole());
        if (utilisateur.getStationService() != null) {
            this.stationServiceId = utilisateur.getStationService().getId();
        }
    }
}