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

import com.icesi.bu_app.model.Status;
import com.icesi.bu_app.repository.IStatusRepository;
import com.icesi.bu_app.service.impl.StatusService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StatusServiceTest {

    @Mock
    private IStatusRepository repository;
    @InjectMocks
    private StatusService service;

    private Status status;

    @BeforeEach
    void setUp() {
        status = new Status();
        status.setId(1);
        status.setStatus("ACTIVE");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(status));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Optional.of(status));
        assertEquals("ACTIVE", service.findById(1).getStatus());
    }

    @Test
    void testSave() {
        when(repository.save(any(Status.class))).thenReturn(status);
        assertNotNull(service.save(status));
    }

    @Test
    void testUpdate() {
        when(repository.findById(1)).thenReturn(Optional.of(status));
        when(repository.save(any(Status.class))).thenReturn(status);

        Status updated = new Status();
        updated.setStatus("INACTIVE");

        Status result = service.update(1, updated);

        assertNotNull(result);
        verify(repository).save(any(Status.class));
    }

    @Test
    void testDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(status));
        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }
}