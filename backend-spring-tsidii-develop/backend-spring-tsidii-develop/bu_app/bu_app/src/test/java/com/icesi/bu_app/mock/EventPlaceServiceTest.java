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

import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.EventPlace;
import com.icesi.bu_app.model.Place;
import com.icesi.bu_app.repository.IEventPlaceRepository;
import com.icesi.bu_app.repository.IEventRepository;
import com.icesi.bu_app.repository.IPlaceRepository;
import com.icesi.bu_app.service.impl.EventPlaceService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EventPlaceServiceTest {

    @Mock
    private IEventPlaceRepository eventPlaceRepository;
    @Mock
    private IEventRepository eventRepository;
    @Mock
    private IPlaceRepository placeRepository;
    @InjectMocks
    private EventPlaceService eventPlaceService;

    private Event event;
    private Place place;
    private EventPlace eventPlace;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1);
        event.setName("Meeting");
        event.setDescription("Planning");

        place = new Place();
        place.setId(1);
        place.setName("Room A");

        eventPlace = new EventPlace();
        eventPlace.setId(1);
        eventPlace.setStartDate(new Timestamp(0));
        eventPlace.setEndDate(new Timestamp(1000));
        eventPlace.setEvent(event);
        eventPlace.setPlace(place);
    }

    @Test
    void testFindAll() {
        when(eventPlaceRepository.findAll()).thenReturn(List.of(eventPlace));
        List<EventPlace> result = eventPlaceService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(eventPlaceRepository.findById(1)).thenReturn(Optional.of(eventPlace));
        EventPlace result = eventPlaceService.findById(1);
        assertEquals(1, result.getId());
    }

    @Test
    void testSave() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(placeRepository.findById(1)).thenReturn(Optional.of(place));
        when(eventPlaceRepository.save(any(EventPlace.class))).thenReturn(eventPlace);

        EventPlace saved = eventPlaceService.save(eventPlace);

        assertNotNull(saved);
        verify(eventPlaceRepository).save(any(EventPlace.class));
    }

    @Test
    void testUpdate() {
        when(eventPlaceRepository.findById(1)).thenReturn(Optional.of(eventPlace));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(placeRepository.findById(1)).thenReturn(Optional.of(place));
        when(eventPlaceRepository.save(any(EventPlace.class))).thenReturn(eventPlace);

        EventPlace updated = new EventPlace();
        updated.setStartDate(new Timestamp(2000));
        updated.setEndDate(new Timestamp(3000));
        updated.setEvent(event);
        updated.setPlace(place);

        EventPlace result = eventPlaceService.update(1, updated);

        assertNotNull(result);
        verify(eventPlaceRepository).save(any(EventPlace.class));
    }

    @Test
    void testDelete() {
        when(eventPlaceRepository.findById(1)).thenReturn(Optional.of(eventPlace));
        doNothing().when(eventPlaceRepository).deleteById(1);

        eventPlaceService.delete(1);

        verify(eventPlaceRepository).deleteById(1);
    }
}