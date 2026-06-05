package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Exercise;

public interface IExerciseService {
    Exercise findById(Integer id);

    List<Exercise> findAll();

    List<Exercise> findAllCustom();

    List<Exercise> findAllPredefined();

    Page<Exercise> findAll(int page, int size);

    Exercise save(Exercise exercise);

    Exercise update(Integer id, Exercise exercise);

    void delete(Integer id);
}