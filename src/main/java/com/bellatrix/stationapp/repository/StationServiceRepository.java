package com.bellatrix.stationapp.repository;

import com.bellatrix.stationapp.model.StationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationServiceRepository extends JpaRepository<StationService, Long> {
    Optional<StationService> findByNom(String name);
    // Aucune méthode personnalisée requise pour l'instant

}
