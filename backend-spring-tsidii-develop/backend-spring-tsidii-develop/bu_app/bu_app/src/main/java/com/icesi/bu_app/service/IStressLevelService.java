package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.StressLevel;

public interface IStressLevelService {
    StressLevel findById(Integer id);

    List<StressLevel> findAll();

    Page<StressLevel> findAll(int page, int size);

    StressLevel save(StressLevel stressLevel);

    StressLevel update(Integer id, StressLevel stressLevel);

    void delete(Integer id);
}