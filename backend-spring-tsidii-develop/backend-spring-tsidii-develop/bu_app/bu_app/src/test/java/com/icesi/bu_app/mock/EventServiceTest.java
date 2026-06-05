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

import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IEventRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.impl.EventService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EventServiceTest {

    @Mock
    private IEventRepository eventRepository;
    @Mock
    private IUserRepository userRepository;
    @InjectMocks
    private EventService eventService;

    private Event event;
    private User manager;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setCode(1);

        event = new Event();
        event.setId(1);
        event.setName("Conference");
        event.setDescription("Annual conference");
        event.setManager(manager);
    }

    @Test
    void testFindAll() {
        when(eventRepository.findAll()).thenReturn(List.of(event));
        List<Event> result = eventService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        Event result = eventService.findById(1);
        assertEquals("Conference", result.getName());
    }

    @Test
    void testSave() {
        when(userRepository.findById(1)).thenReturn(Optional.of(manager));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event saved = eventService.save(event);

        assertNotNull(saved);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testUpdate() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(userRepository.findById(1)).thenReturn(Optional.of(manager));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updated = new Event();
        updated.setName("Updated Conference");
        updated.setDescription("Updated description");
        updated.setManager(manager);

        Event result = eventService.update(1, updated);

        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testDelete() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(1);

        eventService.delete(1);

        verify(eventRepository).deleteById(1);
    }
}