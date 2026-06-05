package com.icesi.bu_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.icesi.bu_app.model.Alert;

@Repository
public interface IAlertRepository extends JpaRepository<Alert, Integer> {
    // Lo mismo que para las recomendaciones
    List<Alert> findByTrainerCode(Integer trainerCode);

    List<Alert> findByTraineeCode(Integer traineeCode);
}
