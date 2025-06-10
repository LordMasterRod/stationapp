// src/main/java/com/bellatrix/stationapp/repository/UtilisateurRepository.java
package com.bellatrix.stationapp.repository;

import com.bellatrix.stationapp.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username); // Très important pour Spring Security
    boolean existsByUsername(String username); // Utile pour l'enregistrement
}