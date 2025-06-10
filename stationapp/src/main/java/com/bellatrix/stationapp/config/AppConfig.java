// src/main/java/com/bellatrix/stationapp/config/AppConfig.java
package com.bellatrix.stationapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean // Définit le PasswordEncoder comme un bean autonome
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}