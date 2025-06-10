package com.bellatrix.stationapp.controller;

import com.bellatrix.stationapp.model.Client;
import com.bellatrix.stationapp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indique que cette classe est un contrôleur REST
@RequestMapping("/api/clients") // Définit le chemin de base pour toutes les requêtes de ce contrôleur
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Endpoint pour créer un nouveau client.
     * Requête: POST /api/clients
     * Corps de la requête (JSON): { "numeroTelephone": "...", "nom": "...", "prenom": "..." }
     * Réponse: Client créé avec statut 201 CREATED
     */
    @PostMapping
    public ResponseEntity<?> creerClient(@RequestBody Client client) {
        try {
            Client nouveauClient = clientService.creerClient(
                    client.getNumeroTelephone(),
                    client.getNom(),
                    client.getPrenom()
            );
            return new ResponseEntity<>(nouveauClient, HttpStatus.CREATED); // Statut 201 CREATED
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Statut 409 CONFLICT si client existe déjà
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur interne du serveur lors de la création du client.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint pour obtenir un client par son ID.
     * Requête: GET /api/clients/{id}
     * Réponse: Client trouvé avec statut 200 OK, ou 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint pour obtenir un client par son numéro de téléphone.
     * Requête: GET /api/clients/by-phone?numeroTelephone=...
     * Réponse: Client trouvé avec statut 200 OK, ou 404 NOT FOUND
     */
    @GetMapping("/by-phone")
    public ResponseEntity<Client> getClientByNumeroTelephone(@RequestParam String numeroTelephone) {
        Optional<Client> client = clientService.getClientByNumeroTelephone(numeroTelephone);
        return client.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients(); // Cette méthode n'existe pas encore dans ClientService, nous allons l'ajouter
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }
}
