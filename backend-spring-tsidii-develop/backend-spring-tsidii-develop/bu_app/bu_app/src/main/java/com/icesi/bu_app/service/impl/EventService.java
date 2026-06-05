package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.repository.IEventRepository;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.service.IEventService;
import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final IEventRepository eventRepository;
    private final IUserRepository userRepository;

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Page<Event> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return eventRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Event findById(Integer id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Override
    public Event save(Event event) {
        event.setManager(resolveRequiredUser(event.getManager()));
        return eventRepository.save(event);
    }

    @Override
    public Event update(Integer id, Event event) {
        Event foundEvent = findById(id);
        foundEvent.setName(event.getName());
        foundEvent.setDescription(event.getDescription());
        foundEvent.setManager(resolveRequiredUser(event.getManager()));
        return eventRepository.save(foundEvent);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        eventRepository.deleteById(id);
    }

    private User resolveRequiredUser(User user) {
        if (user == null || user.getCode() == null) {
            throw new BadRequestException("Event must have a manager");
        }
        return userRepository.findById(user.getCode())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}