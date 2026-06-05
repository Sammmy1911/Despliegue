package com.icesi.bu_app.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.icesi.bu_app.model.Alert;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IAlertRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.impl.AlertService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AlertServiceTest {

    @Mock
    private IAlertRepository alertRepository;
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private AlertService alertService;

    private Alert alert;
    private User trainer;
    private User trainee;

    @BeforeEach
    void setUp() {
        trainer = new User();
        trainer.setCode(1);
        trainee = new User();
        trainee.setCode(2);

        alert = new Alert();
        alert.setId(1);
        alert.setMessage("Reminder");
        alert.setSendDate(new Timestamp(0));
        alert.setTrainer(trainer);
        alert.setTrainee(trainee);
    }

    @Test
    void testFindAll() {
        when(alertRepository.findAll()).thenReturn(List.of(alert));
        List<Alert> result = alertService.findAll();
        assertEquals(1, result.size());
        assertEquals("Reminder", result.get(0).getMessage());
    }

    @Test
    void testFindById() {
        when(alertRepository.findById(1)).thenReturn(Optional.of(alert));
        Alert result = alertService.findById(1);
        assertNotNull(result);
        assertEquals("Reminder", result.getMessage());
    }

    @Test
    void testSave() {
        when(userRepository.findById(1)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(2)).thenReturn(Optional.of(trainee));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        Alert saved = alertService.save(alert);

        assertNotNull(saved);
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void testUpdate() {
        when(alertRepository.findById(1)).thenReturn(Optional.of(alert));
        when(userRepository.findById(1)).thenReturn(Optional.of(trainer));
        when(userRepository.findById(2)).thenReturn(Optional.of(trainee));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        Alert updated = new Alert();
        updated.setMessage("Updated");
        updated.setSendDate(new Timestamp(1));
        updated.setTrainer(trainer);
        updated.setTrainee(trainee);

        Alert result = alertService.update(1, updated);

        assertNotNull(result);
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void testDelete() {
        when(alertRepository.findById(1)).thenReturn(Optional.of(alert));
        doNothing().when(alertRepository).deleteById(1);

        alertService.delete(1);

        verify(alertRepository).findById(1);
        verify(alertRepository).deleteById(1);
    }
}