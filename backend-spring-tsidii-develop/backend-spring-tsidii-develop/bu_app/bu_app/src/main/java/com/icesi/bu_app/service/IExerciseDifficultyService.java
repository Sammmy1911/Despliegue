package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.ExerciseDifficulty;

public interface IExerciseDifficultyService {
    ExerciseDifficulty findById(Integer id);

    List<ExerciseDifficulty> findAll();

    Page<ExerciseDifficulty> findAll(int page, int size);

    ExerciseDifficulty save(ExerciseDifficulty exerciseDifficulty);

    ExerciseDifficulty update(Integer id, ExerciseDifficulty exerciseDifficulty);

    void delete(Integer id);
}