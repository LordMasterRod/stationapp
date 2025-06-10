// src/main/java/com/bellatrix/stationapp/controller/AuthController.java
package com.bellatrix.stationapp.controller;

import com.bellatrix.stationapp.model.Role;
import com.bellatrix.stationapp.model.Utilisateur;
import com.bellatrix.stationapp.security.JwtService;
import com.bellatrix.stationapp.service.UtilisateurService;
import com.bellatrix.stationapp.model.CarteFidelite;
import com.bellatrix.stationapp.service.CarteFideliteService;
import com.bellatrix.stationapp.payload.request.ClientLoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j; // NOUVEAU: Import pour l'annotation @Slf4j

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:8081"})
@Slf4j // NOUVEAU: Annotation Lombok pour le logging
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurService userService;
    private final CarteFideliteService carteFideliteService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UtilisateurService userService,
                          CarteFideliteService carteFideliteService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.carteFideliteService = carteFideliteService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUtilisateur(@RequestBody Map<String, String> loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.get("username"), loginRequest.get("password"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Utilisateur utilisateurDetails = (Utilisateur) authentication.getPrincipal();
            String jwt = jwtService.generateToken(utilisateurDetails.getUsername(), utilisateurDetails.getRole().name());
            String roleName = utilisateurDetails.getRole().name();
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "isAuthenticated", true,
                    "id", utilisateurDetails.getId(),
                    "username", utilisateurDetails.getUsername(),
                    "role", roleName
            ));
        } catch (Exception e) {
            log.error("Authentication error for utilisateur: {}", e.getMessage(), e); // Utilisation de log.error avec stack trace
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Identifiants invalides"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUtilisateur(@RequestBody Map<String, String> registerRequest) {
        try {
            Role defaultRole = Role.CLIENT_WEB;
            Utilisateur nouvelUtilisateur = userService.registerNewUtilisateur(
                    registerRequest.get("username"),
                    registerRequest.get("password"),
                    defaultRole
            );
            return new ResponseEntity<>("Utilisateur enregistré avec succès: " + nouvelUtilisateur.getUsername(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Registration error: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Unexpected error during user registration: {}", e.getMessage(), e);
            return new ResponseEntity<>("Erreur lors de l'enregistrement de l'utilisateur.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // NOUVEL ENDPOINT : Connexion pour les clients via le numéro de carte de fidélité (AVEC LOGS DE DÉBUG)
    @PostMapping("/client-login")
    public ResponseEntity<?> authenticateClientByCard(@RequestBody ClientLoginRequest clientLoginRequest) {
        log.info("--- Début de la requête /api/auth/client-login ---");
        String numeroCarte = clientLoginRequest.getNumeroCarte();
        log.info("Numéro de carte reçu : {}", numeroCarte);

        try {
            // 1. Trouver la carte de fidélité par le numéro de carte
            Optional<CarteFidelite> optionalCarte = carteFideliteService.getCarteFideliteByNumeroCarte(numeroCarte);
            log.debug("Résultat de la recherche de carte : {}", optionalCarte.isPresent() ? "trouvée" : "non trouvée");

            if (optionalCarte.isEmpty()) {
                log.warn("Carte de fidélité introuvable pour le numéro : {}", numeroCarte);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Numéro de carte de fidélité introuvable."));
            }

            CarteFidelite carte = optionalCarte.get();
            log.debug("Carte de fidélité trouvée, ID : {}", carte.getId());
            log.debug("Statut actif de la carte : {}", carte.getActive());

            // 2. Vérifier si la carte est active
            if (!carte.getActive()) {
                log.warn("Tentative de connexion avec une carte inactive pour le numéro : {}", numeroCarte);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Votre carte de fidélité est inactive."));
            }

            // Récupération de l'ID du client lié à la carte
            Long clientId = carte.getClient().getId(); // C'est ici que l'erreur pourrait se produire si getClient() est null
            log.debug("ID du client associé à la carte : {}", clientId);

            // 3. Générer le JWT avec les informations du client
            String jwt = jwtService.generateToken(numeroCarte, Role.CLIENT_WEB.name());
            log.debug("JWT généré pour le numéro de carte : {}", numeroCarte);

            // 4. Retourner la réponse
            log.info("Connexion client réussie pour le numéro de carte : {}", numeroCarte);
            return ResponseEntity.ok(Map.of(
                    "token", jwt,
                    "isAuthenticated", true,
                    "id", clientId,
                    "username", numeroCarte,
                    "role", Role.CLIENT_WEB.name()
            ));
        } catch (Exception e) {
            // Loguer l'exception complète ici
            log.error("Erreur inattendue lors de la connexion client pour le numéro de carte {}: {}", numeroCarte, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Une erreur interne est survenue. Veuillez réessayer plus tard."));
        } finally {
            log.info("--- Fin de la requête /api/auth/client-login ---");
        }
    }
}