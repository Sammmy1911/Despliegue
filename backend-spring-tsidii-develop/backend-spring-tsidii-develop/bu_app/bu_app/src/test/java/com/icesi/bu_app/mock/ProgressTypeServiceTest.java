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

import com.icesi.bu_app.model.ProgressType;
import com.icesi.bu_app.repository.IProgressTypeRepository;
import com.icesi.bu_app.service.impl.ProgressTypeService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProgressTypeServiceTest {

    @Mock
    private IProgressTypeRepository repository;
    @InjectMocks
    private ProgressTypeService service;

    private ProgressType progressType;

    @BeforeEach
    void setUp() {
        progressType = new ProgressType();
        progressType.setId(1);
        progressType.setType("Weekly");
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(progressType));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Optional.of(progressType));
        assertEquals("Weekly", service.findById(1).getType());
    }

    @Test
    void testSave() {
        when(repository.save(any(ProgressType.class))).thenReturn(progressType);
        assertNotNull(service.save(progressType));
    }

    @Test
    void testUpdate() {
        when(repository.findById(1)).thenReturn(Optional.of(progressType));
        when(repository.save(any(ProgressType.class))).thenReturn(progressType);

        ProgressType updated = new ProgressType();
        updated.setType("Monthly");

        ProgressType result = service.update(1, updated);

        assertNotNull(result);
        verify(repository).save(any(ProgressType.class));
    }

    @Test
    void testDelete() {
        when(repository.findById(1)).thenReturn(Optional.of(progressType));
        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }
}