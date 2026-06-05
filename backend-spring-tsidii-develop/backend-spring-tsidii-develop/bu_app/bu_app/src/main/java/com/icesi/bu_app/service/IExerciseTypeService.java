package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.ExerciseType;

public interface IExerciseTypeService {
    ExerciseType findById(Integer id);

    List<ExerciseType> findAll();

    Page<ExerciseType> findAll(int page, int size);

    ExerciseType save(ExerciseType exerciseType);

    ExerciseType update(Integer id, ExerciseType exerciseType);

    void delete(Integer id);
}