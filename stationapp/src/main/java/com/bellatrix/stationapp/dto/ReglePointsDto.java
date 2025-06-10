package com.bellatrix.stationapp.dto;

import com.bellatrix.stationapp.model.ReglePoints;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Pour la conversion si nécessaire
import java.time.format.DateTimeFormatter; // Pour formater LocalDateTime en String

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglePointsDto {
    private Long id;
    private Double pointsParLitre;
    private String dateDebut; // Représenté comme une String (ISO 8601)
    private String dateFin;   // Représenté comme une String (ISO 8601)
    private Boolean active;

    // Constructeur pour mapper l'entité ReglePoints vers ce DTO
    public ReglePointsDto(ReglePoints reglePoints) {
        this.id = reglePoints.getId();
        this.pointsParLitre = reglePoints.getPointsParLitre();
        // Formater LocalDateTime en String ISO 8601 pour le frontend
        this.dateDebut = reglePoints.getDateDebut() != null ? reglePoints.getDateDebut().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        this.dateFin = reglePoints.getDateFin() != null ? reglePoints.getDateFin().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        this.active = reglePoints.getActive();
    }

    // Méthode utilitaire pour convertir le DTO en entité (pour les requêtes POST/PUT)
    public ReglePoints toEntity() {
        ReglePoints reglePoints = new ReglePoints();
        reglePoints.setId(this.id);
        reglePoints.setPointsParLitre(this.pointsParLitre);
        // Parser String ISO 8601 en LocalDateTime
        reglePoints.setDateDebut(this.dateDebut != null ? LocalDateTime.parse(this.dateDebut, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        reglePoints.setDateFin(this.dateFin != null ? LocalDateTime.parse(this.dateFin, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        reglePoints.setActive(this.active);
        return reglePoints;
    }
}
