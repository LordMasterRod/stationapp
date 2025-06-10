// src/main/java/com/bellatrix/stationapp/service/ReglePointService.java
package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.ReglePoints;
import com.bellatrix.stationapp.repository.ReglePointRepository; // Assure-toi d'importer ton repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReglePointService {

    private final ReglePointRepository reglePointRepository;

    @Autowired
    public ReglePointService(ReglePointRepository reglePointRepository) {
        this.reglePointRepository = reglePointRepository;
    }

    /**
     * Crée une nouvelle règle de points ou met à jour la règle existante si elle est active.
     * Si 'active' est true, désactive les autres règles actives avant de sauvegarder.
     *
     * @param pointsParLitre Le nombre de points par litre.
     * @param dateDebut La date à laquelle la règle devient active.
     * @param dateFin La date à laquelle la règle cesse d'être active (peut être null).
     * @param active Indique si la règle est actuellement active.
     * @return La règle de points créée ou mise à jour.
     */
    @Transactional
    public ReglePoints creerOuMettreAJourRegle(Double pointsParLitre, LocalDateTime dateDebut, LocalDateTime dateFin, boolean active) {
        if (active) {
            // Désactive toutes les règles actuellement actives
            reglePointRepository.findByActive(true).ifPresent(r -> {
                r.setActive(false);
                r.setDateFin(LocalDateTime.now()); // Date de fin pour l'ancienne règle
                reglePointRepository.save(r);
            });
        }

        ReglePoints nouvelleRegle = new ReglePoints();
        nouvelleRegle.setPointsParLitre(pointsParLitre);
        nouvelleRegle.setDateDebut(dateDebut);
        nouvelleRegle.setDateFin(dateFin);
        nouvelleRegle.setActive(active);
        return reglePointRepository.save(nouvelleRegle);
    }

    /**
     * Récupère la règle de points actuellement active.
     * C'est la méthode que le DataLoader utilise pour vérifier l'existence.
     *
     * @return Un Optional contenant la règle de points active si elle existe, sinon vide.
     */
    @Transactional(readOnly = true)
    public Optional<ReglePoints> getRegleActive() {
        // Supposons que tu as une méthode findByActive(boolean active) dans ton repository.
        // Assure-toi que cette méthode est bien définie dans ReglePointRepository.
        return reglePointRepository.findByActive(true);
    }

    /**
     * Récupère une règle de points par son ID.
     *
     * @param id L'ID de la règle de points.
     * @return Un Optional contenant la règle de points si trouvée, sinon vide.
     */
    @Transactional(readOnly = true)
    public Optional<ReglePoints> getReglePointById(Long id) {
        return reglePointRepository.findById(id);
    }

    /**
     * Récupère toutes les règles de points (actives et inactives).
     *
     * @return Une liste de toutes les règles de points.
     */
    @Transactional(readOnly = true)
    public List<ReglePoints> getAllReglesPoints() {
        return reglePointRepository.findAll();
    }

    /**
     * Met à jour une règle de points existante.
     *
     * @param reglePoint Les informations de la règle à mettre à jour.
     * @return La règle de points mise à jour.
     * @throws IllegalArgumentException si la règle n'existe pas.
     */
    @Transactional
    public ReglePoints majReglePoint(ReglePoints reglePoint) {
        if (!reglePointRepository.existsById(reglePoint.getId())) {
            throw new IllegalArgumentException("Règle de points avec l'ID " + reglePoint.getId() + " non trouvée.");
        }
        return reglePointRepository.save(reglePoint);
    }

    /**
     * Supprime une règle de points par son ID.
     *
     * @param id L'ID de la règle de points à supprimer.
     */
    @Transactional
    public void supprimerReglePoint(Long id) {
        if (!reglePointRepository.existsById(id)) {
            throw new IllegalArgumentException("Règle de points avec l'ID " + id + " non trouvée.");
        }
        reglePointRepository.deleteById(id);
    }
}