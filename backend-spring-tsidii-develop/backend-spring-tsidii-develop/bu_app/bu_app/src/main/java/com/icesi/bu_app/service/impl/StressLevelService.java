package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.StressLevel;
import com.icesi.bu_app.repository.IStressLevelRepository;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.service.IStressLevelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StressLevelService implements IStressLevelService {

    private final IStressLevelRepository stressLevelRepository;

    @Override
    public List<StressLevel> findAll() {
        return stressLevelRepository.findAll();
    }

    @Override
    public Page<StressLevel> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return stressLevelRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public StressLevel findById(Integer id) {
        return stressLevelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stress level not found"));
    }

    @Override
    public StressLevel save(StressLevel stressLevel) {
        return stressLevelRepository.save(stressLevel);
    }

    @Override
    public StressLevel update(Integer id, StressLevel stressLevel) {
        StressLevel foundStressLevel = findById(id);
        foundStressLevel.setLevel(stressLevel.getLevel());
        return stressLevelRepository.save(foundStressLevel);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        stressLevelRepository.deleteById(id);
    }
}