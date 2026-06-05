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

import com.icesi.bu_app.model.StressLevel;
import com.icesi.bu_app.repository.IStressLevelRepository;
import com.icesi.bu_app.service.impl.StressLevelService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StressLevelServiceTest {

    @Mock
    private IStressLevelRepository repository;
    @InjectMocks
    private StressLevelService service;

    private StressLevel stressLevel;

    @BeforeEach
    void setUp() {
        stressLevel = new StressLevel();
        stressLevel.setId(1);
        stressLevel.setLevel("High");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(stressLevel));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Optional.of(stressLevel));
        assertEquals("High", service.findById(1).getLevel());
    }

    @Test
    void testSave() {
        when(repository.save(any(StressLevel.class))).thenReturn(stressLevel);
        assertNotNull(service.save(stressLevel));
    }

    @Test
    void testUpdate() {
        when(repository.findById(1)).thenReturn(Optional.of(stressLevel));
        when(repository.save(any(StressLevel.class))).thenReturn(stressLevel);

        StressLevel updated = new StressLevel();
        updated.setLevel("Low");

        StressLevel result = service.update(1, updated);

        assertNotNull(result);
        verify(repository).save(any(StressLevel.class));
    }

    @Test
    void testDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(stressLevel));
        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }
}