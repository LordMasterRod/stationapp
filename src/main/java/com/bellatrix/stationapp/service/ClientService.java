// src/main/java/com/bellatrix/stationapp/service/ClientService.java
package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.Client;
import com.bellatrix.stationapp.repository.ClientRepository;
import com.bellatrix.stationapp.dto.ClientDto; // Importez votre ClientDto existant
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Indique que cette classe est un composant de service Spring
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired // Injection de dépendance du ClientRepository
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional // Assure que la méthode s'exécute dans une transaction de base de données
    public Client creerClient(String numeroTelephone, String nom, String prenom) {
        // Vérifier si un client avec ce numéro existe déjà
        if (clientRepository.findByNumeroTelephone(numeroTelephone).isPresent()) {
            throw new IllegalArgumentException("Un client avec ce numéro de téléphone existe déjà.");
        }

        Client client = new Client();
        client.setNumeroTelephone(numeroTelephone);
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setDateInscription(LocalDateTime.now());
        client.setSoldePoints(0.0); // Nouveau client commence avec 0 points
        return clientRepository.save(client); // Sauvegarde le client dans la base de données
    }

    @Transactional(readOnly = true) // Lecture seule, optimisation des transactions
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll(); // findAll() est fourni par JpaRepository
    }

    @Transactional(readOnly = true)
    public Optional<Client> getClientByNumeroTelephone(String numeroTelephone) {
        return clientRepository.findByNumeroTelephone(numeroTelephone);
    }

    @Transactional
    public Client mettreAJourSoldePoints(Client client, Double points) {
        // Logique existante pour mettre à jour les points
        client.setSoldePoints(client.getSoldePoints() + points);
        return clientRepository.save(client);
    }

    // --- NOUVELLES MÉTHODES AJOUTÉES ICI ---

    // Méthode pour obtenir un ClientDto par ID, utilisée par le contrôleur
    @Transactional(readOnly = true)
    public Optional<ClientDto> getClientDtoById(Long id) {
        return clientRepository.findById(id)
                .map(ClientDto::new); // Utilise le constructeur de votre ClientDto
    }
}