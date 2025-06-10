package com.bellatrix.stationapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationServiceDto {
    private Long id;
    private String nom;
    private String adresse;
    private String ville;

    // Constructeur pour mapper l'entité StationService vers ce DTO
    public StationServiceDto(com.bellatrix.stationapp.model.StationService stationService) {
        this.id = stationService.getId();
        this.nom = stationService.getNom();
        this.adresse = stationService.getAdresse();
        this.ville = stationService.getVille();
    }
}