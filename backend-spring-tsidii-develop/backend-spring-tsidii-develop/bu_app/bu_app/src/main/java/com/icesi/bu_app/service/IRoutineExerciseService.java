package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.RoutineExercise;

public interface IRoutineExerciseService {
    RoutineExercise findById(Integer id);

    List<RoutineExercise> findAll();

    Page<RoutineExercise> findAll(int page, int size);

    RoutineExercise save(RoutineExercise routineExercise);

    RoutineExercise update(Integer id, RoutineExercise routineExercise);

    void delete(Integer id);
}