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

import com.icesi.bu_app.model.Place;
import com.icesi.bu_app.model.Status;
import com.icesi.bu_app.repository.IPlaceRepository;
import com.icesi.bu_app.repository.IStatusRepository;
import com.icesi.bu_app.service.impl.PlaceService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PlaceServiceTest {

    @Mock
    private IPlaceRepository placeRepository;
    @Mock
    private IStatusRepository statusRepository;
    @InjectMocks
    private PlaceService service;

    private Place place;
    private Status status;

    @BeforeEach
    void setUp() {
        status = new Status();
        status.setId(1);
        status.setStatus("ACTIVE");

        place = new Place();
        place.setId(1);
        place.setName("Gym");
        place.setStatus(status);
    }

    @Test
    void testFindAll() {
        when(placeRepository.findAll()).thenReturn(List.of(place));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById() {
        when(placeRepository.findById(1)).thenReturn(Optional.of(place));
        assertEquals("Gym", service.findById(1).getName());
    }

    @Test
    void testSave() {
        when(statusRepository.findById(1)).thenReturn(Optional.of(status));
        when(placeRepository.save(any(Place.class))).thenReturn(place);

        assertNotNull(service.save(place));
    }

    @Test
    void testUpdate() {
        when(placeRepository.findById(1)).thenReturn(Optional.of(place));
        when(statusRepository.findById(1)).thenReturn(Optional.of(status));
        when(placeRepository.save(any(Place.class))).thenReturn(place);

        Place updated = new Place();
        updated.setName("New Gym");
        updated.setStatus(status);

        Place result = service.update(1, updated);

        assertNotNull(result);
        verify(placeRepository).save(any(Place.class));
    }

    @Test
    void testDelete() {
        when(placeRepository.findById(1)).thenReturn(Optional.of(place));
        doNothing().when(placeRepository).deleteById(1);

        service.delete(1);

        verify(placeRepository).deleteById(1);
    }
}