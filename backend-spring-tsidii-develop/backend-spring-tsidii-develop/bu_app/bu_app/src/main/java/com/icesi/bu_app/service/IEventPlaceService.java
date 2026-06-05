package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.EventPlace;

public interface IEventPlaceService {
    EventPlace findById(Integer id);

    List<EventPlace> findAll();

    Page<EventPlace> findAll(int page, int size);

    EventPlace save(EventPlace eventPlace);

    EventPlace update(Integer id, EventPlace eventPlace);

    void delete(Integer id);
}