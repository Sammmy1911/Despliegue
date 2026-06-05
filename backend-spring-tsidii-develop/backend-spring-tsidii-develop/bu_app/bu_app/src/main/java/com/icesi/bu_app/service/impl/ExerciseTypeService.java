package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.model.ExerciseType;
import com.icesi.bu_app.repository.IExerciseTypeRepository;
import com.icesi.bu_app.service.IExerciseTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseTypeService implements IExerciseTypeService {

    private final IExerciseTypeRepository exerciseTypeRepository;

    @Override
    public List<ExerciseType> findAll() {
        return exerciseTypeRepository.findAll();
    }

    @Override
    public Page<ExerciseType> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return exerciseTypeRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public ExerciseType findById(Integer id) {
        return exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found"));
    }

    @Override
    public ExerciseType save(ExerciseType exerciseType) {
        return exerciseTypeRepository.save(exerciseType);
    }

    @Override
    public ExerciseType update(Integer id, ExerciseType exerciseType) {
        ExerciseType foundExerciseType = findById(id);
        foundExerciseType.setName(exerciseType.getName());
        return exerciseTypeRepository.save(foundExerciseType);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        exerciseTypeRepository.deleteById(id);
    }
}