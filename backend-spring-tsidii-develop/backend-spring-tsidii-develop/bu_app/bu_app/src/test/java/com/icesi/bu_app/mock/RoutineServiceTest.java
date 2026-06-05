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

import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IRoutineRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.impl.RoutineService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RoutineServiceTest {

    @Mock
    private IRoutineRepository routineRepository;
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private RoutineService service;

    private Routine routine;
    private User trainer;
    private User trainee;

    @BeforeEach
    void setUp() {
        trainer = new User();
        trainer.setCode(1);
        trainee = new User();
        trainee.setCode(2);

        routine = new Routine();
        routine.setId(1);
        routine.setName("Morning Routine");
        routine.setTrainer(trainer);
        routine.setTrainee(trainee);
    }

    @Test
    void testFindAll() {
        when(routineRepository.findAll()).thenReturn(List.of(routine));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        assertEquals("Morning Routine", service.findById(1).getName());
    }

    @Test
    void testSave() {
        when(userRepository.findById(1)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(2)).thenReturn(Optional.of(trainee));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        assertNotNull(service.save(routine));
    }

    @Test
    void testUpdate() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(userRepository.findById(1)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(2)).thenReturn(Optional.of(trainee));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        Routine updated = new Routine();
        updated.setName("Evening Routine");
        updated.setTrainer(trainer);
        updated.setTrainee(trainee);

        Routine result = service.update(1, updated);

        assertNotNull(result);
        verify(routineRepository).save(any(Routine.class));
    }

    @Test
    void testDelete() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        doNothing().when(routineRepository).deleteById(1);

        service.delete(1);

        verify(routineRepository).deleteById(1);
    }
}