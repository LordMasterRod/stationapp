// src/main/java/com/bellatrix/stationapp/service/UtilisateurService.java
package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.Role;
import com.bellatrix.stationapp.model.Utilisateur;
import com.bellatrix.stationapp.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder; // Correctement injecté

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Méthodes pour Spring Security (loadUserByUsername est essentielle) ---

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // --- LOG DE DEBUG ICI ---
        System.out.println("--- DEBUG AUTH (loadUserByUsername) ---");
        System.out.println("Attempting to load user for authentication: '" + username + "'");
        // --- FIN LOG DE DEBUG ---

        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // --- LOG DE DEBUG ICI EN CAS D'ERREUR ---
                    System.err.println("ERROR: User not found during authentication attempt: '" + username + "'");
                    // --- FIN LOG DE DEBUG ---
                    return new UsernameNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur: " + username);
                });
    }

    @Transactional
    public Utilisateur registerNewUtilisateur(String username, String password, Role role) {
        if (utilisateurRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris.");
        }

        // --- LOGS DE DEBUG ICI ---
        System.out.println("--- DEBUG HASHING (registerNewUtilisateur) ---");
        System.out.println("Username to register: '" + username + "'");
        // !!! ATTENTION: NE JAMAIS LOGGUER UN MOT DE PASSE EN CLAIR EN PRODUCTION !!!
        // C'est uniquement pour le débogage.
        System.out.println("Password (plain) received for hashing: '" + password + "'");
        String hashedPassword = passwordEncoder.encode(password);
        System.out.println("Hashed password (generated): '" + hashedPassword + "'");
        System.out.println("----------------------------------------------");
        // --- FIN LOGS DE DEBUG ---

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setUsername(username);
        nouvelUtilisateur.setPasswordHash(hashedPassword);
        nouvelUtilisateur.setRole(role);
        nouvelUtilisateur.setActive(true); // Par défaut actif
        return utilisateurRepository.save(nouvelUtilisateur);
    }

    // --- Méthodes CRUD pour la gestion des utilisateurs (utilisées par le contrôleur) ---

    @Transactional
    public Utilisateur creerUtilisateur(String username, String password, Role role, Long aLong) {
        // Cette méthode délègue la création à registerNewUtilisateur pour la gestion du hachage et de l'unicité
        // Le paramètre 'aLong' semble inutilisé ici.
        return registerNewUtilisateur(username, password, role);
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> getUtilisateurByUsername(String username) {
        return utilisateurRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @Transactional
    public Utilisateur majUtilisateur(Utilisateur utilisateur) {
        if (!utilisateurRepository.existsById(utilisateur.getId())) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + utilisateur.getId() + " non trouvé.");
        }
        Utilisateur existingUser = utilisateurRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));
        utilisateur.setPasswordHash(existingUser.getPasswordHash());
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void supprimerUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur avec l'ID " + id + " non trouvé.");
        }
        utilisateurRepository.deleteById(id);
    }

    @Transactional
    public Utilisateur majRoleUtilisateur(Long id, Role newRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        utilisateur.setRole(newRole);
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur majActiveStatusUtilisateur(Long id, boolean active) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        utilisateur.setActive(active);
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur changerMotDePasse(Long id, String newPassword) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        utilisateur.setPasswordHash(passwordEncoder.encode(newPassword));
        return utilisateurRepository.save(utilisateur);
    }
}