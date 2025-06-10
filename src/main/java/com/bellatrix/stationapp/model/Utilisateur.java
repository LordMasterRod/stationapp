package com.bellatrix.stationapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur implements UserDetails { // <<< Implémente UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // Utilisons un Enum pour les rôles, c'est plus robuste et sécurisé que String
    // Créez une Enum 'Role' si ce n'est pas déjà fait (voir ci-dessous)
    @Enumerated(EnumType.STRING) // Stocke l'enum comme une chaîne dans la DB
    @Column(name = "role", nullable = false, length = 50) // Ex: 'ADMIN', 'STATION_EMPLOYEE', 'CLIENT_WEB'
    private Role role; // Change le type en 'Role' (ton Enum)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_service_id") // Peut être null pour un admin global
    private StationService stationService;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // --- Implémentation des méthodes UserDetails ---
    // Ces méthodes sont nécessaires pour Spring Security
    // Elles ne devraient pas impacter ton application desktop.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security attend un préfixe "ROLE_" pour les rôles
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Le nom d'utilisateur est déjà dans ton champ 'username'
    @Override
    public String getUsername() {
        return username;
    }

    // Le mot de passe haché est déjà dans ton champ 'passwordHash'
    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // À adapter si tu gères l'expiration des comptes
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // À adapter si tu gères le blocage de compte (ex: après X tentatives échouées)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // À adapter si tu forces les utilisateurs à changer de mot de passe périodiquement
    }

    @Override
    public boolean isEnabled() {
        return this.active; // Utilise ton champ 'active' pour déterminer si le compte est activé
    }
}