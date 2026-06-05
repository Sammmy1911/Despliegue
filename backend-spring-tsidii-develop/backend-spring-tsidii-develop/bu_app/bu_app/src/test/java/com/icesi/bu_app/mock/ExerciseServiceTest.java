package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Blob;
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
import com.icesi.bu_app.model.ExerciseDifficulty;
import com.icesi.bu_app.model.ExerciseType;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IExerciseDifficultyRepository;
import com.icesi.bu_app.repository.IExerciseRepository;
import com.icesi.bu_app.repository.IExerciseTypeRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.impl.ExerciseService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ExerciseServiceTest {

    @Mock
    private IExerciseRepository exerciseRepository;
    @Mock
    private IExerciseDifficultyRepository difficultyRepository;
    @Mock
    private IExerciseTypeRepository typeRepository;
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private ExerciseService service;

    private Exercise exercise;
    private ExerciseDifficulty difficulty;
    private ExerciseType type;
    private User owner;

    @BeforeEach
    void setUp() {
        difficulty = new ExerciseDifficulty();
        difficulty.setId(1);
        difficulty.setName("Medium");

        type = new ExerciseType();
        type.setId(1);
        type.setName("Cardio");

        owner = new User();
        owner.setCode(1);

        exercise = new Exercise();
        exercise.setId(1);
        exercise.setName("Jumping Jacks");
        exercise.setLength(10);
        exercise.setDescription("Warmup");
        exercise.setVideo(mock(Blob.class));
        exercise.setCustom(true);
        exercise.setDifficulty(difficulty);
        exercise.setType(type);
        exercise.setOwner(owner);
    }

    @Test
    void testFindAll() {
        when(exerciseRepository.findAll()).thenReturn(List.of(exercise));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        assertEquals("Jumping Jacks", service.findById(1).getName());
    }

    @Test
    void testSave() {
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        assertNotNull(service.save(exercise));
    }

    @Test
    void testSavePredefinedExerciseDoesNotNeedOwner() {
        Exercise predefined = new Exercise();
        predefined.setName("Push Ups");
        predefined.setLength(12);
        predefined.setDescription("Bodyweight exercise");
        predefined.setCustom(false);
        predefined.setDifficulty(difficulty);
        predefined.setType(type);

        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise saved = service.save(predefined);

        assertFalse(saved.isCustom());
        assertNull(saved.getOwner());
        assertNull(saved.getVideo());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testUpdateWithoutVideoKeepsExistingVideo() {
        Blob existingVideo = mock(Blob.class);
        exercise.setVideo(existingVideo);

        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise updated = new Exercise();
        updated.setName("Burpees");
        updated.setLength(15);
        updated.setDescription("Harder");
        updated.setVideo(null);
        updated.setCustom(true);
        updated.setDifficulty(difficulty);
        updated.setType(type);
        updated.setOwner(owner);

        Exercise result = service.update(1, updated);

        assertNotNull(result);
        assertSame(existingVideo, result.getVideo());
    }

    @Test
    void testSaveCustomExerciseWithoutOwnerFails() {
        Exercise custom = new Exercise();
        custom.setName("My Routine");
        custom.setLength(12);
        custom.setDescription("Custom");
        custom.setVideo(mock(Blob.class));
        custom.setCustom(true);
        custom.setDifficulty(difficulty);
        custom.setType(type);

        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));

        assertThrows(RuntimeException.class, () -> service.save(custom));
    }

    @Test
    void testUpdate() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        when(difficultyRepository.findById(1)).thenReturn(Optional.of(difficulty));
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        Exercise updated = new Exercise();
        updated.setName("Burpees");
        updated.setLength(15);
        updated.setDescription("Harder");
        updated.setVideo(mock(Blob.class));
        updated.setCustom(true);
        updated.setDifficulty(difficulty);
        updated.setType(type);
        updated.setOwner(owner);

        Exercise result = service.update(1, updated);

        assertNotNull(result);
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void testDelete() {
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(exercise));
        doNothing().when(exerciseRepository).deleteById(1);

        service.delete(1);

        verify(exerciseRepository).deleteById(1);
    }
}