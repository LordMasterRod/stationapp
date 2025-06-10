// src/main/java/com/bellatrix/stationapp/controller/TransactionController.java
package com.bellatrix.stationapp.controller;

import com.bellatrix.stationapp.model.Client;
import com.bellatrix.stationapp.model.StationService;
import com.bellatrix.stationapp.model.Utilisateur;
import com.bellatrix.stationapp.dto.TransactionCarburantDto;
import com.bellatrix.stationapp.dto.ClientDto; // Important: utilisez votre ClientDto pour le solde de points
import com.bellatrix.stationapp.model.TransactionCarburant;
import com.bellatrix.stationapp.service.ClientService; // Assurez-vous d'importer ClientService
import com.bellatrix.stationapp.service.StationServiceService;
import com.bellatrix.stationapp.service.TransactionService;
import com.bellatrix.stationapp.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Si vous utilisez Spring Security
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final ClientService clientService; // ClientService est essentiel ici
    private final StationServiceService stationServiceService;
    private final UtilisateurService utilisateurService;

    @Autowired
    public TransactionController(TransactionService transactionService,
                                 ClientService clientService,
                                 StationServiceService stationServiceService,
                                 UtilisateurService utilisateurService) {
        this.transactionService = transactionService;
        this.clientService = clientService;
        this.stationServiceService = stationServiceService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'STATION_EMPLOYEE')")
    public ResponseEntity<List<TransactionCarburantDto>> getAllTransactions() {
        List<TransactionCarburantDto> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/achat")
    // @PreAuthorize("hasAnyAuthority('STATION_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> enregistrerAchatCarburant(@RequestBody Map<String, Object> payload) {
        try {
            Long clientId = Long.valueOf(payload.get("clientId").toString());
            Long stationServiceId = Long.valueOf(payload.get("stationServiceId").toString());
            Long utilisateurId = Long.valueOf(payload.get("utilisateurId").toString());
            Double litresAchetes = Double.valueOf(payload.get("litresAchetes").toString());
            Double montantTotal = Double.valueOf(payload.get("montantTotal").toString());
            Boolean utiliserPoints = (Boolean) payload.getOrDefault("utiliserPoints", false);

            // Charger les entités complètes depuis leurs services pour la logique métier
            Client client = clientService.getClientById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Client introuvable."));
            StationService station = stationServiceService.getStationServiceById(stationServiceId)
                    .orElseThrow(() -> new IllegalArgumentException("Station-service introuvable."));
            Utilisateur utilisateur = utilisateurService.getUtilisateurById(utilisateurId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

            // Appel au service transactionnel
            TransactionCarburant transaction = transactionService.enregistrerAchatCarburant(
                    client, station, utilisateur, litresAchetes, montantTotal, utiliserPoints
            );

            // Mapper l'entité sauvegardée vers un DTO avant de la renvoyer
            TransactionCarburantDto transactionDto = new TransactionCarburantDto(transaction);
            return new ResponseEntity<>(transactionDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur interne du serveur lors de l'enregistrement de la transaction: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // NOUVEL ENDPOINT : Récupérer les transactions pour un client spécifique
    // Path: /api/transactions/client/{clientId}
    @GetMapping("/client/{clientId}")
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT_WEB', 'STATION_EMPLOYEE')")
    public ResponseEntity<List<TransactionCarburantDto>> getTransactionsByClientId(@PathVariable Long clientId) {
        try {
            List<TransactionCarburantDto> transactions = transactionService.getTransactionsByClientId(clientId);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Client non trouvé
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // NOUVEL ENDPOINT : Récupérer le solde de points d'un client
    // Path: /api/transactions/clients/{clientId}/points
    @GetMapping("/clients/{clientId}/points") // Le chemin '/clients' dans '/api/transactions/clients' est conventionnel ici
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT_WEB')")
    public ResponseEntity<ClientDto> getClientPointsBalance(@PathVariable Long clientId) {
        try {
            // Utilise ClientService pour obtenir le solde de points et mapper en ClientDto
            ClientDto clientDto = clientService.getClientDtoById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec l'ID: " + clientId));
            return new ResponseEntity<>(clientDto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // NOUVEAUX ENDPOINTS : Rapports de transactions

    // Rapport Journalier
    // Path: /api/transactions/reports/daily?date=YYYY-MM-DD
    @GetMapping("/reports/daily")
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'STATION_EMPLOYEE')")
    public ResponseEntity<List<TransactionCarburantDto>> getDailyReport(@RequestParam String date) {
        try {
            LocalDate reportDate = LocalDate.parse(date);
            List<TransactionCarburantDto> transactions = transactionService.getDailyTransactionsReport(reportDate);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rapport Hebdomadaire
    // Path: /api/transactions/reports/weekly?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
    @GetMapping("/reports/weekly")
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'STATION_EMPLOYEE')")
    public ResponseEntity<List<TransactionCarburantDto>> getWeeklyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<TransactionCarburantDto> transactions = transactionService.getWeeklyTransactionsReport(start, end);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rapport Mensuel
    // Path: /api/transactions/reports/monthly?year=YYYY&month=MM
    @GetMapping("/reports/monthly")
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'STATION_EMPLOYEE')")
    public ResponseEntity<List<TransactionCarburantDto>> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<TransactionCarburantDto> transactions = transactionService.getMonthlyTransactionsReport(year, month);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rapport Annuel
    // Path: /api/transactions/reports/annual?year=YYYY
    @GetMapping("/reports/annual")
    // @PreAuthorize("hasAnyAuthority('ADMIN', 'STATION_EMPLOYEE')")
    public ResponseEntity<List<TransactionCarburantDto>> getAnnualReport(@RequestParam int year) {
        try {
            List<TransactionCarburantDto> transactions = transactionService.getAnnualTransactionsReport(year);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}