package com.bellatrix.stationapp.model; // Assurez-vous que le package correspond au vôtre

import jakarta.persistence.*; // Utilisez jakarta.persistence pour Spring Boot 3+
import lombok.Data; // Importe Lombok pour les getters/setters, etc.
import lombok.NoArgsConstructor; // Pour le constructeur sans arguments
import lombok.AllArgsConstructor; // Pour le constructeur avec tous les arguments

import java.time.LocalDateTime; // Pour gérer les dates et heures

@Entity // Indique que cette classe est une entité JPA
@Table(name = "client") // Mappe cette entité à la table 'client'
@Data // Génère automatiquement les getters, setters, toString, equals et hashCode (Lombok)
@NoArgsConstructor // Génère un constructeur sans arguments (Lombok)
@AllArgsConstructor // Génère un constructeur avec tous les arguments (Lombok)
public class Client {

    @Id // Indique que 'id' est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Spécifie la stratégie d'auto-incrémentation
    private Long id;

    @Column(name = "numero_telephone", unique = true, nullable = false, length = 20)
    private String numeroTelephone;

    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    @Column(name = "solde_points", nullable = false)
    private Double soldePoints = 0.0; // Valeur par défaut à 0.0
}