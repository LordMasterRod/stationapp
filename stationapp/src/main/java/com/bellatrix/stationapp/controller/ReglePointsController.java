package com.bellatrix.stationapp.controller;

import com.bellatrix.stationapp.dto.ReglePointsDto;
import com.bellatrix.stationapp.model.ReglePoints;
import com.bellatrix.stationapp.service.ReglePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Pour la sécurité
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Pour gérer les dates
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/regles-points") // Chemin de base pour ce contrôleur
public class ReglePointsController {

    private final ReglePointService reglePointService;

    @Autowired
    public ReglePointsController(ReglePointService reglePointService) {
        this.reglePointService = reglePointService;
    }

    @GetMapping
    // @PreAuthorize("hasAuthority('ADMIN')") // Décommentez ceci pour réactiver la sécurité
    public ResponseEntity<List<ReglePointsDto>> getAllReglesPoints() {
        List<ReglePoints> regles = reglePointService.getAllReglesPoints();
        List<ReglePointsDto> dtos = regles.stream()
                .map(ReglePointsDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('ADMIN')") // Décommentez ceci pour réactiver la sécurité
    public ResponseEntity<ReglePointsDto> getReglePointById(@PathVariable Long id) {
        return reglePointService.getReglePointById(id)
                .map(ReglePointsDto::new)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    // @PreAuthorize("hasAuthority('ADMIN')") // Décommentez ceci pour réactiver la sécurité
    public ResponseEntity<ReglePointsDto> createReglePoint(@RequestBody ReglePointsDto reglePointsDto) {
        try {
            // Convertir le DTO en entité pour le service
            ReglePoints nouvelleRegle = reglePointsDto.toEntity();
            // Utiliser la méthode existante du service
            ReglePoints createdRegle = reglePointService.creerOuMettreAJourRegle(
                    nouvelleRegle.getPointsParLitre(),
                    nouvelleRegle.getDateDebut(),
                    nouvelleRegle.getDateFin(),
                    nouvelleRegle.getActive()
            );
            return new ResponseEntity<>(new ReglePointsDto(createdRegle), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('ADMIN')") // Décommentez ceci pour réactiver la sécurité
    public ResponseEntity<ReglePointsDto> updateReglePoint(@PathVariable Long id, @RequestBody ReglePointsDto reglePointsDto) {
        try {
            // Assurez-vous que l'ID de l'entité correspond à l'ID du chemin
            reglePointsDto.setId(id);
            ReglePoints regleToUpdate = reglePointsDto.toEntity();
            ReglePoints updatedRegle = reglePointService.majReglePoint(regleToUpdate);
            return new ResponseEntity<>(new ReglePointsDto(updatedRegle), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('ADMIN')") // Décommentez ceci pour réactiver la sécurité
    public ResponseEntity<Void> deleteReglePoint(@PathVariable Long id) {
        try {
            reglePointService.supprimerReglePoint(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
