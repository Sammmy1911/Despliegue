package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.EventPlace;
import com.icesi.bu_app.model.Place;
import com.icesi.bu_app.repository.IEventPlaceRepository;
import com.icesi.bu_app.repository.IEventRepository;
import com.icesi.bu_app.repository.IPlaceRepository;
import com.icesi.bu_app.service.IEventPlaceService;
import com.icesi.bu_app.exception.BadRequestException;
import com.icesi.bu_app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventPlaceService implements IEventPlaceService {

    private final IEventPlaceRepository eventPlaceRepository;
    private final IEventRepository eventRepository;
    private final IPlaceRepository placeRepository;

    @Override
    public List<EventPlace> findAll() {
        return eventPlaceRepository.findAll();
    }

    @Override
    public Page<EventPlace> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return eventPlaceRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public EventPlace findById(Integer id) {
        return eventPlaceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("EventPlace not found"));
    }

    @Override
    public EventPlace save(EventPlace eventPlace) {
        eventPlace.setEvent(resolveRequiredEvent(eventPlace.getEvent()));
        eventPlace.setPlace(resolveRequiredPlace(eventPlace.getPlace()));
        return eventPlaceRepository.save(eventPlace);
    }

    @Override
    public EventPlace update(Integer id, EventPlace eventPlace) {
        EventPlace foundEventPlace = findById(id);
        foundEventPlace.setStartDate(eventPlace.getStartDate());
        foundEventPlace.setEndDate(eventPlace.getEndDate());
        foundEventPlace.setEvent(resolveRequiredEvent(eventPlace.getEvent()));
        foundEventPlace.setPlace(resolveRequiredPlace(eventPlace.getPlace()));
        return eventPlaceRepository.save(foundEventPlace);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        eventPlaceRepository.deleteById(id);
    }

    private Event resolveRequiredEvent(Event event) {
        if (event == null || event.getId() == null) {
            throw new BadRequestException("EventPlace must have an event");
        }
        return eventRepository.findById(event.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    private Place resolveRequiredPlace(Place place) {
        if (place == null || place.getId() == null) {
            throw new BadRequestException("EventPlace must have a place");
        }
        return placeRepository.findById(place.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Place not found"));
    }
}