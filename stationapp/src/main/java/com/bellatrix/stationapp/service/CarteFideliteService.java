// src/main/java/com/bellatrix/stationapp/service/CarteFideliteService.java
package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.CarteFidelite;
import com.bellatrix.stationapp.model.Client;
import com.bellatrix.stationapp.repository.CarteFideliteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List; // Assurez-vous que cet import est présent
import java.util.Optional;

@Service
public class CarteFideliteService {

    private final CarteFideliteRepository carteFideliteRepository;

    @Autowired
    public CarteFideliteService(CarteFideliteRepository carteFideliteRepository) {
        this.carteFideliteRepository = carteFideliteRepository;
    }

    /**
     * Crée et sauvegarde une nouvelle carte de fidélité.
     * Le numéro de carte doit être unique.
     *
     * @param client Le client auquel la carte est associée.
     * @param numeroCarte Le numéro unique de la carte (6 chiffres).
     * @param points Le nombre initial de points sur la carte.
     * @return La carte de fidélité créée et sauvegardée.
     * @throws IllegalArgumentException si une carte avec ce numéro existe déjà ou si le client possède déjà une carte.
     */
    @Transactional
    public CarteFidelite creerCarteFidelite(Client client, String numeroCarte, Double points) {
        if (carteFideliteRepository.findByNumeroCarte(numeroCarte).isPresent()) {
            throw new IllegalArgumentException("Une carte avec ce numéro existe déjà.");
        }
        if (carteFideliteRepository.findByClient(client).isPresent()) {
            throw new IllegalArgumentException("Ce client possède déjà une carte de fidélité.");
        }

        CarteFidelite carte = new CarteFidelite();
        carte.setNumeroCarte(numeroCarte);
        carte.setClient(client);
        carte.setSoldePoints(points); // Utilise le champ soldePoints
        carte.setDateEmission(LocalDateTime.now());
        carte.setActive(true);
        return carteFideliteRepository.save(carte);
    }

    /**
     * Récupère une carte de fidélité par son numéro de carte.
     *
     * @param numeroCarte Le numéro de carte à rechercher.
     * @return Un Optional contenant la carte de fidélité si trouvée, sinon vide.
     */
    @Transactional(readOnly = true)
    public Optional<CarteFidelite> getCarteFideliteByNumeroCarte(String numeroCarte) {
        return carteFideliteRepository.findByNumeroCarte(numeroCarte);
    }

    /**
     * Récupère une carte de fidélité associée à un client donné.
     *
     * @param client Le client dont on veut récupérer la carte de fidélité.
     * @return Un Optional contenant la carte de fidélité si trouvée, sinon vide.
     */
    @Transactional(readOnly = true)
    public Optional<CarteFidelite> getCarteFideliteByClient(Client client) {
        return carteFideliteRepository.findByClient(client);
    }

    /**
     * Récupère une carte de fidélité par son ID.
     *
     * @param id L'ID de la carte de fidélité.
     * @return Un Optional contenant la carte de fidélité si trouvée, sinon vide.
     */
    @Transactional(readOnly = true)
    public Optional<CarteFidelite> getCarteFideliteById(Long id) {
        return carteFideliteRepository.findById(id);
    }

    /**
     * Récupère toutes les cartes de fidélité.
     *
     * @return Une liste de toutes les cartes de fidélité.
     */
    @Transactional(readOnly = true)
    public List<CarteFidelite> getAllCarteFidelites() {
        return carteFideliteRepository.findAll();
    }

    /**
     * Met à jour une carte de fidélité existante.
     *
     * @param carteFidelite Les informations de la carte à mettre à jour.
     * @return La carte de fidélité mise à jour.
     * @throws IllegalArgumentException si la carte n'existe pas.
     */
    @Transactional
    public CarteFidelite majCarteFidelite(CarteFidelite carteFidelite) {
        if (!carteFideliteRepository.existsById(carteFidelite.getId())) {
            throw new IllegalArgumentException("Carte de fidélité avec l'ID " + carteFidelite.getId() + " non trouvée.");
        }
        return carteFideliteRepository.save(carteFidelite);
    }

    /**
     * Met à jour les points d'une carte de fidélité.
     *
     * @param id L'ID de la carte.
     * @param points Les nouveaux points de la carte.
     * @return La carte de fidélité mise à jour.
     * @throws IllegalArgumentException si la carte n'existe pas.
     */
    @Transactional
    public CarteFidelite majPointsCarteFidelite(Long id, Double points) {
        CarteFidelite carte = carteFideliteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carte de fidélité avec l'ID " + id + " non trouvée."));
        carte.setSoldePoints(points); // Utilise le champ soldePoints
        return carteFideliteRepository.save(carte);
    }

    /**
     * Active ou désactive une carte de fidélité.
     *
     * @param id L'ID de la carte.
     * @param active Le nouvel état (true pour actif, false pour inactif).
     * @return La carte de fidélité mise à jour.
     * @throws IllegalArgumentException si la carte n'existe pas.
     */
    @Transactional
    public CarteFidelite majStatutActiveCarteFidelite(Long id, boolean active) {
        CarteFidelite carte = carteFideliteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carte de fidélité avec l'ID " + id + " non trouvée."));
        carte.setActive(active);
        return carteFideliteRepository.save(carte);
    }

    /**
     * Supprime une carte de fidélité par son ID.
     *
     * @param id L'ID de la carte de fidélité à supprimer.
     */
    @Transactional
    public void supprimerCarteFidelite(Long id) {
        if (!carteFideliteRepository.existsById(id)) {
            throw new IllegalArgumentException("Carte de fidélité avec l'ID " + id + " non trouvée.");
        }
        carteFideliteRepository.deleteById(id);
    }
}