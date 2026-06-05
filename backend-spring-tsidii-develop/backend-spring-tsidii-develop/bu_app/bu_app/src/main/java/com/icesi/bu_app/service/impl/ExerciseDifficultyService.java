package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.model.ExerciseDifficulty;
import com.icesi.bu_app.repository.IExerciseDifficultyRepository;
import com.icesi.bu_app.service.IExerciseDifficultyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseDifficultyService implements IExerciseDifficultyService {

    private final IExerciseDifficultyRepository exerciseDifficultyRepository;

    @Override
    public List<ExerciseDifficulty> findAll() {
        return exerciseDifficultyRepository.findAll();
    }

    @Override
    public Page<ExerciseDifficulty> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return exerciseDifficultyRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public ExerciseDifficulty findById(Integer id) {
        return exerciseDifficultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise difficulty not found"));
    }

    @Override
    public ExerciseDifficulty save(ExerciseDifficulty exerciseDifficulty) {
        return exerciseDifficultyRepository.save(exerciseDifficulty);
    }

    @Override
    public ExerciseDifficulty update(Integer id, ExerciseDifficulty exerciseDifficulty) {
        ExerciseDifficulty foundExerciseDifficulty = findById(id);
        foundExerciseDifficulty.setName(exerciseDifficulty.getName());
        return exerciseDifficultyRepository.save(foundExerciseDifficulty);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        exerciseDifficultyRepository.deleteById(id);
    }
}