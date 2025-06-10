package com.bellatrix.stationapp.repository;

import com.bellatrix.stationapp.model.SeuilRachat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeuilRachatRepository extends JpaRepository<SeuilRachat, Long> {
    // Méthode pour obtenir tous les seuils de rachat triés par points requis
    List<SeuilRachat> findAllByOrderByPointsRequisAsc();
}
