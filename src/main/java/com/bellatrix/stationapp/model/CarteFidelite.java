package com.bellatrix.stationapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "carte_fidelite")
@Data // Lombok génère les getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok génère un constructeur sans arguments
@AllArgsConstructor // Lombok génère un constructeur avec tous les arguments
public class CarteFidelite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOUVEAU: Le champ 'numeroCarte' est maintenant le numéro à 6 chiffres
    @Column(name = "numero_carte", unique = true, nullable = false, length = 6)
    private String numeroCarte; // Le champ renommé pour plus de clarté

    // Relation ManyToOne avec Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission;

    @Column(name = "active", nullable = false)
    private Boolean active = true; // Valeur par défaut à true

    // NOUVEAU CHAMP POUR LES POINTS, bien placé avec les autres
    @Column(name = "solde_points", nullable = false)
    private Double soldePoints = 0.0; // Initialiser à 0.0 par défaut


}