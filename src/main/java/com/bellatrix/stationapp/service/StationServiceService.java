package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.StationService;
import com.bellatrix.stationapp.repository.StationServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StationServiceService {

    private final StationServiceRepository stationServiceRepository;

    @Autowired
    public StationServiceService(StationServiceRepository stationServiceRepository) {
        this.stationServiceRepository = stationServiceRepository;
    }

    /**
     * Crée et sauvegarde une nouvelle station-service.
     *
     * @param nom L'adresse de la station.
     * @param adresse L'adresse de la station.
     * @param ville La ville où se trouve la station.
     * @return La station-service créée et sauvegardée.
     */
    @Transactional
    public StationService creerStationService(String nom, String adresse, String ville) {
        StationService station = new StationService();
        station.setNom(nom);
        station.setAdresse(adresse);
        station.setVille(ville);
        return stationServiceRepository.save(station);
    }

    /**
     * Récupère une station-service par son ID.
     *
     * @param id L'ID de la station-service.
     * @return Un Optional contenant la station-service si elle existe.
     */
    @Transactional(readOnly = true)
    public Optional<StationService> getStationServiceById(Long id) {
        return stationServiceRepository.findById(id);
    }

    /**
     * Récupère toutes les stations-service.
     *
     * @return Une liste de toutes les stations-service.
     */
    @Transactional(readOnly = true)
    public List<StationService> getAllStationServices() {
        return stationServiceRepository.findAll();
    }

    /**
     * Récupère une station-service par son nom.
     * Cette méthode est cruciale pour le DataLoader afin d'éviter les doublons.
     *
     * @param name Le nom de la station-service à rechercher.
     * @return Un Optional contenant la station-service si elle est trouvée, sinon vide.
     */
    @Transactional(readOnly = true)
    public Optional<StationService> getStationServiceByName(String name) {
        // Tu auras besoin d'ajouter cette méthode à ton StationServiceRepository
        // Exemple: Optional<StationService> findByNom(String nom);
        return stationServiceRepository.findByNom(name);
    }

    /**
     * Met à jour une station-service existante.
     *
     * @param stationService La station-service avec les informations mises à jour.
     * @return La station-service mise à jour.
     * @throws IllegalArgumentException si la station n'existe pas.
     */
    @Transactional
    public StationService majStationService(StationService stationService) {
        if (!stationServiceRepository.existsById(stationService.getId())) {
            throw new IllegalArgumentException("StationService avec l'ID " + stationService.getId() + " non trouvée.");
        }
        return stationServiceRepository.save(stationService);
    }

    /**
     * Supprime une station-service par son ID.
     *
     * @param id L'ID de la station-service à supprimer.
     */
    @Transactional
    public void supprimerStationService(Long id) {
        if (!stationServiceRepository.existsById(id)) {
            throw new IllegalArgumentException("StationService avec l'ID " + id + " non trouvée.");
        }
        stationServiceRepository.deleteById(id);
    }
}