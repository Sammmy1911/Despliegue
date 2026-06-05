package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.RoutineExercise;
import com.icesi.bu_app.repository.IExerciseRepository;
import com.icesi.bu_app.repository.IRoutineExerciseRepository;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.service.impl.RoutineExerciseService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RoutineExerciseServiceTest {

    @Mock
    private IRoutineExerciseRepository routineExerciseRepository;
    @Mock
    private IExerciseRepository exerciseRepository;
    @Mock
    private IRoutineRepository routineRepository;
    @InjectMocks
    private RoutineExerciseService service;

    private RoutineExercise routineExercise;
    private Exercise exercise;
    private Routine routine;

    @BeforeEach
    void setUp() {
        exercise = new Exercise();
        exercise.setId(1);

        routine = new Routine();
        routine.setId(1);

        routineExercise = new RoutineExercise();
        routineExercise.setId(1);
        routineExercise.setExercise(exercise);
        routineExercise.setRoutine(routine);
    }

    @Test
    void testFindAll() {
        when(routineExerciseRepository.findAll()).thenReturn(List.of(routineExercise));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        assertEquals(1, service.findById(1).getId());
    }

    @Test
    void testSave() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(routineExerciseRepository.save(any(RoutineExercise.class))).thenReturn(routineExercise);

        assertNotNull(service.save(routineExercise));
    }

    @Test
    void testUpdate() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(routineExerciseRepository.save(any(RoutineExercise.class))).thenReturn(routineExercise);

        RoutineExercise updated = new RoutineExercise();
        updated.setExercise(exercise);
        updated.setRoutine(routine);

        RoutineExercise result = service.update(1, updated);

        assertNotNull(result);
        verify(routineExerciseRepository).save(any(RoutineExercise.class));
    }

    @Test
    void testDelete() {
        when(routineExerciseRepository.findById(1)).thenReturn(Optional.of(routineExercise));
        doNothing().when(routineExerciseRepository).deleteById(1);

        service.delete(1);

        verify(routineExerciseRepository).deleteById(1);
    }
}