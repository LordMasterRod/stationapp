// src/main/java/com/bellatrix/stationapp/repository/CarteFideliteRepository.java
package com.bellatrix.stationapp.repository;

import com.bellatrix.stationapp.model.CarteFidelite;
import com.bellatrix.stationapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarteFideliteRepository extends JpaRepository<CarteFidelite, Long> {
    // ANCIEN: Optional<CarteFidelite> findByCodeBarres(String codeBarres);
    // NOUVEAU:
    Optional<CarteFidelite> findByNumeroCarte(String numeroCarte); // <-- CHANGEMENT ICI
    Optional<CarteFidelite> findByClient(Client client);
}