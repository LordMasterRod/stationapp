// src/main/java/com/bellatrix/stationapp/repository/TransactionCarburantRepository.java
package com.bellatrix.stationapp.repository;

import com.bellatrix.stationapp.model.Client; // Gardez cette importation
import com.bellatrix.stationapp.model.TransactionCarburant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionCarburantRepository extends JpaRepository<TransactionCarburant, Long> {
    // Nouvelle méthode pour trouver les transactions par l'ID du client
    // Spring Data JPA peut dériver cette requête directement du nom de la méthode
    List<TransactionCarburant> findByClientId(Long clientId); // <-- MODIFIÉ ici pour utiliser directement l'ID

    // Méthodes pour les sommes agrégées (si vous voulez calculer des totaux dans le service)
    @Query("SELECT SUM(t.pointsGagnes) FROM TransactionCarburant t WHERE t.client.id = :clientId")
    Double sumPointsGagnesByClientId(Long clientId);

    @Query("SELECT SUM(t.pointsUtilises) FROM TransactionCarburant t WHERE t.client.id = :clientId")
    Double sumPointsUtilisesByClientId(Long clientId);

    // Requêtes pour les rapports basés sur la date (déjà vu, mais je les remets ici pour clarté)
    List<TransactionCarburant> findByDateTransactionBetween(LocalDateTime startDate, LocalDateTime endDate);
}