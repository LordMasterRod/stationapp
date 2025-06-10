package com.bellatrix.stationapp.controller;


import com.bellatrix.stationapp.model.StationService;
import com.bellatrix.stationapp.service.StationServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stations") // Nouveau chemin de base
public class StationServiceController {

    private final StationServiceService stationServiceService;

    @Autowired
    public StationServiceController(StationServiceService stationServiceService) {
        this.stationServiceService = stationServiceService;
    }

    @PostMapping
    public ResponseEntity<StationService> createStation(@RequestBody StationService station) {
        StationService newStation = stationServiceService.creerStationService(
                station.getNom(), station.getAdresse(), station.getVille()
        );
        return new ResponseEntity<>(newStation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationService> getStationById(@PathVariable Long id) {
        Optional<StationService> station = stationServiceService.getStationServiceById(id);
        return station.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint pour obtenir la liste de toutes les stations-service.
     * Requête: GET /api/stations
     * Réponse: Liste de stations avec statut 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<StationService>> getAllStations() {
        List<StationService> stations = stationServiceService.getAllStationServices();
        return new ResponseEntity<>(stations, HttpStatus.OK);
    }
}
