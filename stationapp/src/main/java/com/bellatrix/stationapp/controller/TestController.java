// src/main/java/com/bellatrix/stationapp/controller/TestController.java
package com.bellatrix.stationapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test") // Le chemin de base pour ce contrôleur
public class TestController {

    @GetMapping("/hello") // Le chemin spécifique pour cette méthode
    public String hello() {
        return "Hello from secure API!";
    }

    @GetMapping // Autre exemple: si tu vas directement sur /api/test sans /hello
    public String rootTest() {
        return "Test root endpoint!";
    }
}