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

import com.icesi.bu_app.model.Progress;
import com.icesi.bu_app.model.ProgressType;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.StressLevel;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IProgressRepository;
import com.icesi.bu_app.repository.IProgressTypeRepository;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.repository.IStressLevelRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.impl.ProgressService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProgressServiceTest {

    @Mock
    private IProgressRepository progressRepository;
    @Mock
    private IStressLevelRepository stressLevelRepository;
    @Mock
    private IProgressTypeRepository progressTypeRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRoutineRepository routineRepository;
    @InjectMocks
    private ProgressService service;

    private Progress progress;
    private StressLevel stressLevel;
    private ProgressType progressType;
    private User trainee;
    private Routine routine;

    @BeforeEach
    void setUp() {
        stressLevel = new StressLevel();
        stressLevel.setId(1);
        stressLevel.setLevel("Low");

        progressType = new ProgressType();
        progressType.setId(1);
        progressType.setType("Daily");

        trainee = new User();
        trainee.setCode(1);

        routine = new Routine();
        routine.setId(1);
        routine.setName("Routine A");

        progress = new Progress();
        progress.setId(1);
        progress.setRepetitions(10);
        progress.setTime("15m");
        progress.setStressLevel(stressLevel);
        progress.setProgressType(progressType);
        progress.setTrainee(trainee);
        progress.setRoutine(routine);
    }

    @Test
    void testFindAll() {
        when(progressRepository.findAll()).thenReturn(List.of(progress));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));
        assertEquals(10, service.findById(1).getRepetitions());
    }

    @Test
    void testSave() {
        when(stressLevelRepository.findById(1)).thenReturn(Optional.of(stressLevel));
        when(progressTypeRepository.findById(1)).thenReturn(Optional.of(progressType));
        when(userRepository.findById(1)).thenReturn(Optional.of(trainee));
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(progressRepository.save(any(Progress.class))).thenReturn(progress);

        assertNotNull(service.save(progress));
    }

    @Test
    void testUpdate() {
        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));
        when(stressLevelRepository.findById(1)).thenReturn(Optional.of(stressLevel));
        when(progressTypeRepository.findById(1)).thenReturn(Optional.of(progressType));
        when(userRepository.findById(1)).thenReturn(Optional.of(trainee));
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(progressRepository.save(any(Progress.class))).thenReturn(progress);

        Progress updated = new Progress();
        updated.setRepetitions(20);
        updated.setTime("20m");
        updated.setStressLevel(stressLevel);
        updated.setProgressType(progressType);
        updated.setTrainee(trainee);
        updated.setRoutine(routine);

        Progress result = service.update(1, updated);

        assertNotNull(result);
        verify(progressRepository).save(any(Progress.class));
    }

    @Test
    void testDelete() {
        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));
        doNothing().when(progressRepository).deleteById(1);

        service.delete(1);

        verify(progressRepository).deleteById(1);
    }
}