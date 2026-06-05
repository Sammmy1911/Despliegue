package com.icesi.bu_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.Exercise;

@Repository
public interface IExerciseRepository extends JpaRepository<Exercise, Integer> {
    List<Exercise> findByType(String type); // cadio, fuerza, etc etc ect

    List<Exercise> findByDifficulty(String difficulty);

    List<Exercise> findByCustomTrue();

    List<Exercise> findByCustomFalse();
}
