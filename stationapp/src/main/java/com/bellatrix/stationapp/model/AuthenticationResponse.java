/*/ src/main/java/com/bellatrix/stationapp/model/AuthenticationResponse.java
package com.bellatrix.stationapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List; // Pour une liste de rôles

@Data // Génère getters, setters, toString, equals, hashCode
@Builder // Permet d'utiliser le pattern Builder pour créer des instances
@AllArgsConstructor // Génère un constructeur avec tous les champs
@NoArgsConstructor // Génère un constructeur sans arguments
public class AuthenticationResponse {

    private String jwtToken;
    private List<String> roles; // Ajouté pour contenir les rôles de l'utilisateur
}*/