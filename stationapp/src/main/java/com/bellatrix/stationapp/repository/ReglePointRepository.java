// src/main/java/com/bellatrix/stationapp/repository/ReglePointRepository.java
package com.bellatrix.stationapp.repository;

import com.bellatrix.stationapp.model.ReglePoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReglePointRepository extends JpaRepository<ReglePoints, Long> {
    // Ajoute cette méthode si elle n'existe pas
    Optional<ReglePoints> findByActive(boolean active);
}