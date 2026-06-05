package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ConflictException;
import com.icesi.bu_app.exception.ResourceNotFoundException;
import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.model.ExerciseDifficulty;
import com.icesi.bu_app.model.ExerciseType;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IExerciseDifficultyRepository;
import com.icesi.bu_app.repository.IExerciseRepository;
import com.icesi.bu_app.repository.IExerciseTypeRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IExerciseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseService implements IExerciseService {

    private final IExerciseRepository exerciseRepository;
    private final IExerciseDifficultyRepository exerciseDifficultyRepository;
    private final IExerciseTypeRepository exerciseTypeRepository;
    private final IUserRepository userRepository;

    @Override
    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    @Override
    public List<Exercise> findAllCustom() {
        return exerciseRepository.findByCustomTrue();
    }

    @Override
    public List<Exercise> findAllPredefined() {
        return exerciseRepository.findByCustomFalse();
    }

    @Override
    public Page<Exercise> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return exerciseRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Exercise findById(Integer id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
    }

    @Override
    public Exercise save(Exercise exercise) {
        validateExerciseFields(exercise);
        exercise.setDifficulty(resolveRequiredDifficulty(exercise.getDifficulty()));
        exercise.setType(resolveRequiredType(exercise.getType()));
        exercise.setCustom(exercise.isCustom());
        exercise.setOwner(resolveOwner(exercise.isCustom(), exercise.getOwner()));
        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise update(Integer id, Exercise exercise) {
        validateExerciseFields(exercise);
        Exercise foundExercise = findById(id);
        foundExercise.setName(exercise.getName());
        foundExercise.setLength(exercise.getLength());
        foundExercise.setDescription(exercise.getDescription());
        if (exercise.getVideo() != null) {
            foundExercise.setVideo(exercise.getVideo());
        }
        foundExercise.setDifficulty(resolveRequiredDifficulty(exercise.getDifficulty()));
        foundExercise.setType(resolveRequiredType(exercise.getType()));
        foundExercise.setCustom(exercise.isCustom());
        foundExercise.setOwner(resolveOwner(exercise.isCustom(), exercise.getOwner()));
        return exerciseRepository.save(foundExercise);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        try {
            exerciseRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(
                    "No se puede eliminar el ejercicio porque está asociado a una rutina. Elimine primero las relaciones rutina-ejercicio.");
        }
    }

    private void validateExerciseFields(Exercise exercise) {
        if (exercise == null) {
            throw new BadRequestException("Exercise payload is required");
        }

        if (exercise.getName() == null || exercise.getName().isBlank()) {
            throw new BadRequestException("Exercise name is required");
        }

        if (exercise.getDescription() == null || exercise.getDescription().isBlank()) {
            throw new BadRequestException("Exercise description is required");
        }

        if (exercise.getDescription().length() > 100) {
            throw new BadRequestException("Exercise description must be at most 100 characters");
        }

        if (exercise.getLength() == null || exercise.getLength() <= 0) {
            throw new BadRequestException("Exercise length must be greater than 0");
        }

    }

    private ExerciseDifficulty resolveRequiredDifficulty(ExerciseDifficulty difficulty) {
        if (difficulty == null || difficulty.getId() == null) {
            throw new BadRequestException("Exercise must have a difficulty");
        }
        return exerciseDifficultyRepository.findById(difficulty.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise difficulty not found"));
    }

    private ExerciseType resolveRequiredType(ExerciseType type) {
        if (type == null || type.getId() == null) {
            throw new BadRequestException("Exercise must have a type");
        }
        return exerciseTypeRepository.findById(type.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found"));
    }

    private User resolveOwner(boolean custom, User user) {
        if (!custom) {
            return null;
        }

        if (user == null || user.getCode() == null) {
            throw new BadRequestException("Custom exercise must have an owner");
        }

        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}