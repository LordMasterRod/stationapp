// src/main/java/com/bellatrix/stationapp/payload/request/ClientLoginRequest.java
package com.bellatrix.stationapp.payload.request;

// Suppression des imports de validation pour simplifier
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;

public class ClientLoginRequest {
    private String numeroCarte; // C'est le champ qui recevra le numéro de carte du client

    // Getters et Setters (vous pouvez les laisser manuels ou ajouter Lombok ici si vous préférez)
    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }
}