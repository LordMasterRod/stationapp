package com.bellatrix.stationapp.service;

import com.bellatrix.stationapp.model.SeuilRachat;
import com.bellatrix.stationapp.repository.SeuilRachatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SeuilRachatService {

    private final SeuilRachatRepository seuilRachatRepository;

    @Autowired
    public SeuilRachatService(SeuilRachatRepository seuilRachatRepository) {
        this.seuilRachatRepository = seuilRachatRepository;
    }

    @Transactional
    public SeuilRachat creerSeuilRachat(Double pointsRequis, Double valeurMonetaire, String description) {
        SeuilRachat seuil = new SeuilRachat();
        seuil.setPointsRequis(pointsRequis);
        seuil.setValeurMonetaire(valeurMonetaire);
        seuil.setDescription(description);
        return seuilRachatRepository.save(seuil);
    }

    @Transactional(readOnly = true)
    public List<SeuilRachat> getAllSeuilsRachat() {
        return seuilRachatRepository.findAllByOrderByPointsRequisAsc();
    }

    @Transactional(readOnly = true)
    public Optional<SeuilRachat> getMeilleurSeuilPourClient(Double soldePointsClient) {
        // Récupère tous les seuils de rachat et trouve le plus élevé que le client peut atteindre
        return getAllSeuilsRachat().stream()
                .filter(seuil -> soldePointsClient >= seuil.getPointsRequis())
                .max(Comparator.comparingDouble(SeuilRachat::getPointsRequis)); // Prend le seuil le plus élevé atteignable
    }

    // D'autres méthodes de mise à jour ou de suppression des seuils peuvent être ajoutées
}
