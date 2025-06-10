package com.bellatrix.stationapp.controller;


import com.bellatrix.stationapp.dto.UtilisateurDto;
import com.bellatrix.stationapp.model.Utilisateur;
import com.bellatrix.stationapp.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Importez PasswordEncoder pour le hachage si vous voulez un endpoint de création ici
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
@RequestMapping("/api/users") // Nouveau chemin de base
public class UtilisateurController {

    private final UtilisateurService utilisateurService ;
    private final PasswordEncoder passwordEncoder ; // Injectez PasswordEncoder ici

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    // Exemple de création d'utilisateur via API (à sécuriser !)

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Utilisateur user) {
        try {
            // Supprimez la ligne de hachage ici. Le service s'en chargera.
            // String hashedPassword = passwordEncoder.encode(user.getPasswordHash()); // <--- SUPPRIMEZ OU COMMENTEZ CETTE LIGNE

            Utilisateur newUser = utilisateurService.creerUtilisateur(
                    user.getUsername(),
                    user.getPasswordHash(), // <--- PASSEZ LE MOT DE PASSE EN CLAIR REÇU DU BODY ICI !
                    user.getRole(),
                    user.getStationService() != null ? user.getStationService().getId() : null
            );
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur interne du serveur lors de la création de l'utilisateur.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUserById(@PathVariable Long id) {
        Optional<Utilisateur> user = utilisateurService.getUtilisateurById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint pour obtenir la liste de tous les utilisateurs de l'application.
     * Requête: GET /api/users
     * Réponse: Liste d'utilisateurs avec statut 200 OK.
     */

    @GetMapping // Endpoint pour récupérer tous les utilisateurs
    //@PreAuthorize("hasAuthority('ADMIN')") // <-- Seuls les ADMINs peuvent accéder à cette méthode
    public List<UtilisateurDto> getAllUsers() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        // Map les entités Utilisateur vers UtilisateurDto pour éviter les problèmes de sérialisation Hibernate
        return utilisateurs.stream()
                .map(UtilisateurDto::new) // Utilise le constructeur de UtilisateurDto qui prend un Utilisateur
                .collect(Collectors.toList());
    }
}
