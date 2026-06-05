package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.RoutineExercise;
import com.icesi.bu_app.repository.IExerciseRepository;
import com.icesi.bu_app.repository.IRoutineExerciseRepository;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.service.IRoutineExerciseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutineExerciseService implements IRoutineExerciseService {

    private final IRoutineExerciseRepository routineExerciseRepository;
    private final IExerciseRepository exerciseRepository;
    private final IRoutineRepository routineRepository;

    @Override
    public List<RoutineExercise> findAll() {
        return routineExerciseRepository.findAll();
    }

    @Override
    public Page<RoutineExercise> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return routineExerciseRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public RoutineExercise findById(Integer id) {
        return routineExerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineExercise not found"));
    }

    @Override
    public RoutineExercise save(RoutineExercise routineExercise) {
        routineExercise.setExercise(resolveRequiredExercise(routineExercise.getExercise()));
        routineExercise.setRoutine(resolveRequiredRoutine(routineExercise.getRoutine()));
        return routineExerciseRepository.save(routineExercise);
    }

    @Override
    public RoutineExercise update(Integer id, RoutineExercise routineExercise) {
        RoutineExercise foundRoutineExercise = findById(id);
        foundRoutineExercise.setExercise(resolveRequiredExercise(routineExercise.getExercise()));
        foundRoutineExercise.setRoutine(resolveRequiredRoutine(routineExercise.getRoutine()));
        return routineExerciseRepository.save(foundRoutineExercise);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        routineExerciseRepository.deleteById(id);
    }

    private Exercise resolveRequiredExercise(Exercise exercise) {
        if (exercise == null || exercise.getId() == null) {
            throw new BadRequestException("RoutineExercise must have an exercise");
        }
        return exerciseRepository.findById(exercise.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
    }

    private Routine resolveRequiredRoutine(Routine routine) {
        if (routine == null || routine.getId() == null) {
            throw new BadRequestException("RoutineExercise must have a routine");
        }
        return routineRepository.findById(routine.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));
    }
}