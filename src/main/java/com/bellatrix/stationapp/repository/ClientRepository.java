package com.bellatrix.stationapp.repository;

// Assurez-vous que le package correspond au vôtre

import com.bellatrix.stationapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Pour la recherche par numéro de téléphone

@Repository // Indique que cette interface est un composant de persistance Spring
public interface ClientRepository extends JpaRepository<Client, Long> {
    // JpaRepository<T, ID> : T est le type de l'entité, ID est le type de sa clé primaire (Long pour Client)

    // Méthode personnalisée pour trouver un client par son numéro de téléphone
    Optional<Client> findByNumeroTelephone(String numeroTelephone);
}
