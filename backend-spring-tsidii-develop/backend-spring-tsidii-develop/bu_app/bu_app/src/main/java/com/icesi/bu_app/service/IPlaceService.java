package com.icesi.bu_app.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.icesi.bu_app.model.Place;

public interface IPlaceService {
    Place findById(Integer id);

    List<Place> findAll();

    Page<Place> findAll(int page, int size);

    Place save(Place place);

    Place update(Integer id, Place place);

    void delete(Integer id);
}