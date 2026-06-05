package com.icesi.bu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.icesi.bu_app.model.Routine;

@Repository
public interface IRoutineRepository extends JpaRepository<Routine, Integer> {
    // Buscar rutinas por código del trainer
    List<Routine> findByTrainerCode(Integer trainerCode);

    // Buscar rutinas por código del trainee
    List<Routine> findByTraineeCode(Integer traineeCode);

    List<Routine> findByName(String name);
}
