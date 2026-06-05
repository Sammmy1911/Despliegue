package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Event;

public interface IEventService {
    Event findById(Integer id);

    List<Event> findAll();

    Page<Event> findAll(int page, int size);

    Event save(Event event);

    Event update(Integer id, Event event);

    void delete(Integer id);
}