package com.bellatrix.stationapp.config;

import com.bellatrix.stationapp.model.*; // Importe tous les modèles
import com.bellatrix.stationapp.service.*; // Importe tous les services nécessaires
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder; // Pour hasher les mots de passe
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Pour gérer l'absence d'utilisateurs

import java.time.LocalDateTime;
import java.util.Optional; // NOUVEAU: Import pour Optional

@Component // Fait de cette classe un composant géré par Spring
public class DataLoader implements CommandLineRunner {

    // Injection des services nécessaires
    private final ClientService clientService;
    private final StationServiceService stationServiceService;
    private final UtilisateurService utilisateurService;
    private final ReglePointService reglePointService;
    private final SeuilRachatService seuilRachatService;
    private final PasswordEncoder passwordEncoder;
    private final CarteFideliteService carteFideliteService;

    // Constructeur pour l'injection de dépendances
    @Autowired
    public DataLoader(ClientService clientService,
                      StationServiceService stationServiceService,
                      UtilisateurService utilisateurService,
                      ReglePointService reglePointService,
                      SeuilRachatService seuilRachatService,
                      PasswordEncoder passwordEncoder,
                      CarteFideliteService carteFideliteService) {
        this.clientService = clientService;
        this.stationServiceService = stationServiceService;
        this.utilisateurService = utilisateurService;
        this.reglePointService = reglePointService;
        this.seuilRachatService = seuilRachatService;
        this.passwordEncoder = passwordEncoder;
        this.carteFideliteService = carteFideliteService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Chargement des données initiales...");

        // 1. Création d'une règle de points
        if (reglePointService.getRegleActive().isEmpty()) {
            reglePointService.creerOuMettreAJourRegle(1.5, LocalDateTime.now(), null, true);
            System.out.println("Règle de points active créée : 1.5 points par litre.");
        } else {
            System.out.println("Règle de points déjà présente.");
        }

        // 2. Création de seuils de rachat
        if (seuilRachatService.getAllSeuilsRachat().isEmpty()) {
            seuilRachatService.creerSeuilRachat(100.0, 5.0, "Réduction de 5€ pour 100 points");
            seuilRachatService.creerSeuilRachat(200.0, 12.0, "Réduction de 12€ pour 200 points");
            seuilRachatService.creerSeuilRachat(500.0, 30.0, "Réduction de 30€ pour 500 points");
            System.out.println("Seuils de rachat créés.");
        } else {
            System.out.println("Seuils de rachat déjà présents.");
        }

        // 3. Création de stations-service
        StationService totalEnergies = null;
        StationService engin = null;
        if (stationServiceService.getStationServiceByName("TotalEnergies Kinshasa").isEmpty()) {
            totalEnergies = stationServiceService.creerStationService("TotalEnergies Kinshasa", "Av. des Huileries", "Kinshasa");
            System.out.println("Station 'TotalEnergies Kinshasa' créée.");
        } else {
            totalEnergies = stationServiceService.getStationServiceByName("TotalEnergies Kinshasa").get();
            System.out.println("Station 'TotalEnergies Kinshasa' déjà présente.");
        }

        if (stationServiceService.getStationServiceByName("Engen Matadi").isEmpty()) {
            engin = stationServiceService.creerStationService("Engen Matadi", "Route de Matadi", "Kinshasa");
            System.out.println("Station 'Engen Matadi' créée.");
        } else {
            engin = stationServiceService.getStationServiceByName("Engen Matadi").get();
            System.out.println("Station 'Engen Matadi' déjà présente.");
        }

        // 4. Création d'utilisateurs de l'application
        createOrGetUtilisateur("admin", "adminpass", Role.ADMIN, null);
        if (totalEnergies != null) {
            Utilisateur stationUser1 = createOrGetUtilisateur("agent1", "agentpass", Role.STATION_EMPLOYEE, null);
            // Si l'utilisateur a été nouvellement créé et qu'il n'avait pas de station service, nous l'assignons.
            // Si l'utilisateur existe déjà, sa station service est déjà assignée ou null.
            if (stationUser1 != null && stationUser1.getStationService() == null) {
                stationUser1.setStationService(totalEnergies);
                utilisateurService.majUtilisateur(stationUser1);
            }
        } else {
            System.err.println("Impossible d'assigner l'agent1: Station TotalEnergies non trouvée/créée.");
        }
        if (engin != null) {
            Utilisateur stationUser2 = createOrGetUtilisateur("agent2", "agentpass", Role.STATION_EMPLOYEE, null);
            if (stationUser2 != null && stationUser2.getStationService() == null) {
                stationUser2.setStationService(engin);
                utilisateurService.majUtilisateur(stationUser2);
            }
        } else {
            System.err.println("Impossible d'assigner l'agent2: Station Engen Matadi non trouvée/créée.");
        }
        createOrGetUtilisateur("clientweb1", "clientpass", Role.CLIENT_WEB, null);
        System.out.println("Utilisateurs de l'application créés ou déjà présents.");

        // 5. Création de clients (pour les tests initiaux)
        Client jeanDupont = null;
        Client marieDurand = null;
        Client paulMartin = null;

        Optional<Client> optionalJean = clientService.getClientByNumeroTelephone("0811234567");
        if (optionalJean.isEmpty()) {
            jeanDupont = clientService.creerClient("0811234567", "Dupont", "Jean");
            System.out.println("Client Jean Dupont créé.");
        } else {
            jeanDupont = optionalJean.get();
            System.out.println("Client Jean Dupont déjà présent.");
        }

        Optional<Client> optionalMarie = clientService.getClientByNumeroTelephone("0829876543");
        if (optionalMarie.isEmpty()) {
            marieDurand = clientService.creerClient("0829876543", "Durand", "Marie");
            System.out.println("Client Marie Durand créé.");
        } else {
            marieDurand = optionalMarie.get();
            System.out.println("Client Marie Durand déjà présente.");
        }

        Optional<Client> optionalPaul = clientService.getClientByNumeroTelephone("0891122334");
        if (optionalPaul.isEmpty()) {
            paulMartin = clientService.creerClient("0891122334", "Martin", "Paul");
            System.out.println("Client Paul Martin créé.");
        } else {
            paulMartin = optionalPaul.get();
            System.out.println("Client Paul Martin déjà présent.");
        }
        System.out.println("Clients initiaux créés ou déjà présents.");


        // 6. Création de cartes de fidélité (pour les tests initiaux)
        // NOUVEAU: Création de cartes avec des numéros à 6 chiffres
        if (jeanDupont != null) {
            if (carteFideliteService.getCarteFideliteByClient(jeanDupont).isEmpty()) {
                // Utilisez un numéro de carte à 6 chiffres
                carteFideliteService.creerCarteFidelite(jeanDupont, "123456", 50.0); // 50 points initiaux
                System.out.println("Carte de fidélité '123456' pour Jean Dupont créée avec 50 points.");
            } else {
                System.out.println("Carte de fidélité pour Jean Dupont déjà présente.");
            }
        } else {
            System.err.println("Client Jean Dupont n'a pas pu être créé/trouvé, impossible de créer la carte.");
        }

        if (marieDurand != null) {
            if (carteFideliteService.getCarteFideliteByClient(marieDurand).isEmpty()) {
                carteFideliteService.creerCarteFidelite(marieDurand, "789012", 120.0); // 120 points initiaux
                System.out.println("Carte de fidélité '789012' pour Marie Durand créée avec 120 points.");
            } else {
                System.out.println("Carte de fidélité pour Marie Durand déjà présente.");
            }
        } else {
            System.err.println("Client Marie Durand n'a pas pu être créé/trouvé, impossible de créer la carte.");
        }

        if (paulMartin != null) {
            if (carteFideliteService.getCarteFideliteByClient(paulMartin).isEmpty()) {
                carteFideliteService.creerCarteFidelite(paulMartin, "345678", 0.0); // 0 points initiaux
                System.out.println("Carte de fidélité '345678' pour Paul Martin créée avec 0 points.");
            } else {
                System.out.println("Carte de fidélité pour Paul Martin déjà présente.");
            }
        } else {
            System.err.println("Client Paul Martin n'a pas pu être créé/trouvé, impossible de créer la carte.");
        }

        // Exemple de carte inactive pour test d'échec
        Client clientInactiveCard = clientService.getClientByNumeroTelephone("0891122334").orElse(null); // Paul Martin
        if (clientInactiveCard != null) {
            Optional<CarteFidelite> cartePaulOpt = carteFideliteService.getCarteFideliteByClient(clientInactiveCard);
            if (cartePaulOpt.isPresent()) {
                CarteFidelite cartePaul = cartePaulOpt.get();
                // Assure que la carte "345678" de Paul Martin est inactive pour les tests
                if (cartePaul.getActive()) {
                    carteFideliteService.majStatutActiveCarteFidelite(cartePaul.getId(), false);
                    System.out.println("Carte de fidélité '345678' de Paul Martin mise à jour comme INACTIVE pour test.");
                } else {
                    System.out.println("Carte de fidélité '345678' de Paul Martin est déjà INACTIVE.");
                }
            }
        }

        System.out.println("Données initiales chargées avec succès.");
    }

    /**
     * Méthode utilitaire pour créer un utilisateur ou le récupérer s'il existe déjà.
     */
    private Utilisateur createOrGetUtilisateur(String username, String password, Role role, StationService stationService) {
        try {
            return (Utilisateur) utilisateurService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            System.out.println("Création de l'utilisateur " + username + "...");
            Utilisateur newUser = utilisateurService.registerNewUtilisateur(username, password, role);
            if (stationService != null) {
                newUser.setStationService(stationService);
                utilisateurService.majUtilisateur(newUser);
            }
            return newUser;
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la création/récupération de l'utilisateur " + username + ": " + e.getMessage());
            return null;
        }
    }
}