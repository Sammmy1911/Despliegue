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

import com.icesi.bu_app.model.ExerciseType;
import com.icesi.bu_app.repository.IExerciseTypeRepository;
import com.icesi.bu_app.service.impl.ExerciseTypeService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ExerciseTypeServiceTest {

    @Mock
    private IExerciseTypeRepository repository;
    @InjectMocks
    private ExerciseTypeService service;

    private ExerciseType type;

    @BeforeEach
    void setUp() {
        type = new ExerciseType();
        type.setId(1);
        type.setName("Strength");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(type));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Optional.of(type));
        assertEquals("Strength", service.findById(1).getName());
    }

    @Test
    void testSave() {
        when(repository.save(any(ExerciseType.class))).thenReturn(type);
        assertNotNull(service.save(type));
    }

    @Test
    void testUpdate() {
        when(repository.findById(1)).thenReturn(Optional.of(type));
        when(repository.save(any(ExerciseType.class))).thenReturn(type);

        ExerciseType updated = new ExerciseType();
        updated.setName("Flexibility");

        ExerciseType result = service.update(1, updated);

        assertNotNull(result);
        verify(repository).save(any(ExerciseType.class));
    }

    @Test
    void testDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(type));
        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }
}