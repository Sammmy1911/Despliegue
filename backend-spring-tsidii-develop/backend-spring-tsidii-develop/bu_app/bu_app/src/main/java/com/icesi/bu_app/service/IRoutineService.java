package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Routine;

public interface IRoutineService {
    Routine findById(Integer id);

    List<Routine> findAll();

    Page<Routine> findAll(int page, int size);

    List<Routine> findByTrainerCode(Integer trainerCode);

    List<Routine> findByTraineeCode(Integer traineeCode);

    Routine save(Routine routine);

    Routine update(Integer id, Routine routine);

    void delete(Integer id);
}