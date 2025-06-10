// src/main/java/com/bellatrix/stationapp/model/Role.java
package com.bellatrix.stationapp.model;

public enum Role {
    ADMIN,              // Pour les administrateurs globaux
    STATION_EMPLOYEE,   // Pour le personnel des stations-service
    CLIENT_WEB          // Nouveau rôle pour les clients qui se connectent via le dashboard web
    // Ajoute d'autres rôles si nécessaire, par exemple DRIVER, MANAGER etc.
}