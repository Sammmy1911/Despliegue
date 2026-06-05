package com.icesi.bu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.ExerciseType;

@Repository
public interface IExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {

}
