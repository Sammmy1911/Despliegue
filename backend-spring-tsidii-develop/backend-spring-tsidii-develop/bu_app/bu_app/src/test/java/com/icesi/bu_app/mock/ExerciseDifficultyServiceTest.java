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

import com.icesi.bu_app.model.ExerciseDifficulty;
import com.icesi.bu_app.repository.IExerciseDifficultyRepository;
import com.icesi.bu_app.service.impl.ExerciseDifficultyService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ExerciseDifficultyServiceTest {

    @Mock
    private IExerciseDifficultyRepository repository;
    @InjectMocks
    private ExerciseDifficultyService service;

    private ExerciseDifficulty difficulty;

    @BeforeEach
    void setUp() {
        difficulty = new ExerciseDifficulty();
        difficulty.setId(1);
        difficulty.setName("Easy");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(difficulty));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Optional.of(difficulty));
        assertEquals("Easy", service.findById(1).getName());
    }

    @Test
    void testSave() {
        when(repository.save(any(ExerciseDifficulty.class))).thenReturn(difficulty);
        assertNotNull(service.save(difficulty));
    }

    @Test
    void testUpdate() {
        when(repository.findById(1)).thenReturn(Optional.of(difficulty));
        when(repository.save(any(ExerciseDifficulty.class))).thenReturn(difficulty);

        ExerciseDifficulty updated = new ExerciseDifficulty();
        updated.setName("Harder");

        ExerciseDifficulty result = service.update(1, updated);

        assertNotNull(result);
        verify(repository).save(any(ExerciseDifficulty.class));
    }

    @Test
    void testDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(difficulty));
        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }
}