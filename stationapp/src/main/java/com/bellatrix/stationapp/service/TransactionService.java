// src/main/java/com/bellatrix/stationapp/service/TransactionService.java
package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.*; // Importe tous les modèles nécessaires
import com.bellatrix.stationapp.repository.TransactionCarburantRepository;
import com.bellatrix.stationapp.dto.TransactionCarburantDto; // Importe le DTO
import com.bellatrix.stationapp.dto.ClientDto; // Importe ClientDto si vous voulez l'utiliser dans un rapport DTO par exemple
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Importe Collectors

@Service
public class TransactionService {

    private final TransactionCarburantRepository transactionCarburantRepository;
    private final ClientService clientService; // Déjà injecté
    private final ReglePointService reglePointService; // Déjà injecté
    private final SeuilRachatService seuilRachatService; // Déjà injecté

    @Autowired
    public TransactionService(TransactionCarburantRepository transactionCarburantRepository,
                              ClientService clientService,
                              ReglePointService reglePointService,
                              SeuilRachatService seuilRachatService) {
        this.transactionCarburantRepository = transactionCarburantRepository;
        this.clientService = clientService;
        this.reglePointService = reglePointService;
        this.seuilRachatService = seuilRachatService;
    }

    @Transactional
    public TransactionCarburant enregistrerAchatCarburant(
            Client client, StationService station, Utilisateur utilisateur,
            Double litresAchetes, Double montantTotal, boolean utiliserPoints) {

        // La logique existante est maintenue, avec une légère modification
        // pour utiliser le soldePoints du client directement pour la vérification des seuils
        if (client == null || station == null || utilisateur == null) {
            throw new IllegalArgumentException("Client, station ou utilisateur ne peuvent pas être nuls.");
        }
        if (litresAchetes <= 0 || montantTotal <= 0) {
            throw new IllegalArgumentException("Les litres achetés et le montant total doivent être positifs.");
        }

        double pointsGagnes = 0.0;
        double pointsUtilises = 0.0;
        double montantReduction = 0.0;

        // 1. Calcul des points gagnés
        ReglePoints regleActive = reglePointService.getRegleActive()
                .orElseThrow(() -> new IllegalStateException("Aucune règle de points active trouvée."));
        pointsGagnes = litresAchetes * regleActive.getPointsParLitre();

        // 2. Gestion de l'utilisation des points (rachat)
        // Utilisation du solde de points actuel du client pour la vérification
        if (utiliserPoints) {
            Optional<SeuilRachat> meilleurSeuil = seuilRachatService.getMeilleurSeuilPourClient(client.getSoldePoints());

            if (meilleurSeuil.isPresent()) {
                SeuilRachat seuil = meilleurSeuil.get();
                if (client.getSoldePoints() >= seuil.getPointsRequis()) {
                    pointsUtilises = seuil.getPointsRequis();
                    montantReduction = seuil.getValeurMonetaire();
                    // Assurez-vous que la réduction ne dépasse pas le montant total
                    if (montantReduction > montantTotal) {
                        montantReduction = montantTotal;
                    }
                    // Déduction des points du solde du client via ClientService
                    // clientService.mettreAJourSoldePoints(client, -pointsUtilises); // Déjà fait dans TransactionService
                } else {
                    throw new IllegalStateException("Le client n'a pas assez de points pour le rachat avec le seuil sélectionné.");
                }
            } else {
                // S'il n'y a pas de seuil de rachat applicable même si utiliserPoints est vrai
                throw new IllegalStateException("Aucun seuil de rachat applicable trouvé.");
            }
        }

        // Mettre à jour le solde du client (déduction des points utilisés ET ajout des points gagnés)
        // La méthode mettreAJourSoldePoints de ClientService doit être appelée ici.
        // Puisque clientService.mettreAJourSoldePoints retourne un Client, nous le mettons à jour.
        // Si des points ont été utilisés, la déduction se fait avant l'ajout des points gagnés.
        double pointsNets = pointsGagnes - pointsUtilises;
        client = clientService.mettreAJourSoldePoints(client, pointsNets);


        // 3. Enregistrement de la transaction
        TransactionCarburant transaction = new TransactionCarburant();
        transaction.setClient(client);
        transaction.setStationService(station);
        transaction.setUtilisateur(utilisateur);
        transaction.setDateTransaction(LocalDateTime.now());
        transaction.setLitresAchetes(litresAchetes);
        transaction.setMontantTotal(montantTotal - montantReduction); // Montant après réduction
        transaction.setPointsGagnes(pointsGagnes);
        transaction.setPointsUtilises(pointsUtilises);

        return transactionCarburantRepository.save(transaction);
    }

    // MODIFIÉ : Retourne une liste de DTOs et assure que les relations sont chargées
    @Transactional(readOnly = true)
    public List<TransactionCarburantDto> getAllTransactions() {
        List<TransactionCarburant> transactions = transactionCarburantRepository.findAll();
        return transactions.stream()
                .map(TransactionCarburantDto::new) // Utilise le constructeur du DTO pour le mappage
                .collect(Collectors.toList());
    }

    // Ajout d'une méthode pour récupérer une transaction par ID et la mapper en DTO
    @Transactional(readOnly = true)
    public Optional<TransactionCarburantDto> getTransactionDtoById(Long id) {
        return transactionCarburantRepository.findById(id)
                .map(TransactionCarburantDto::new);
    }

    // NOUVELLE MÉTHODE : Récupérer les transactions par client ID
    @Transactional(readOnly = true)
    public List<TransactionCarburantDto> getTransactionsByClientId(Long clientId) {
        // Le client n'a pas besoin d'être chargé explicitement ici,
        // le repository peut rechercher directement par client ID.
        // Cependant, pour la vérification de l'existence du client et la cohérence:
        Client client = clientService.getClientById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé avec l'ID: " + clientId));

        return transactionCarburantRepository.findByClientId(clientId).stream() // Utilise findByClientId
                .map(TransactionCarburantDto::new)
                .collect(Collectors.toList());
    }

    // NOUVELLES MÉTHODES : Rapports de transactions

    @Transactional(readOnly = true)
    public List<TransactionCarburantDto> getDailyTransactionsReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return transactionCarburantRepository.findByDateTransactionBetween(startOfDay, endOfDay).stream()
                .map(TransactionCarburantDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionCarburantDto> getWeeklyTransactionsReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startOfWeek = startDate.atStartOfDay();
        LocalDateTime endOfWeek = endDate.atTime(LocalTime.MAX);
        return transactionCarburantRepository.findByDateTransactionBetween(startOfWeek, endOfWeek).stream()
                .map(TransactionCarburantDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionCarburantDto> getMonthlyTransactionsReport(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1); // Dernier jour du mois
        LocalDateTime start = startOfMonth.atStartOfDay();
        LocalDateTime end = endOfMonth.atTime(LocalTime.MAX);
        return transactionCarburantRepository.findByDateTransactionBetween(start, end).stream()
                .map(TransactionCarburantDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionCarburantDto> getAnnualTransactionsReport(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        LocalDateTime start = startOfYear.atStartOfDay();
        LocalDateTime end = endOfYear.atTime(LocalTime.MAX);
        return transactionCarburantRepository.findByDateTransactionBetween(start, end).stream()
                .map(TransactionCarburantDto::new)
                .collect(Collectors.toList());
    }
}