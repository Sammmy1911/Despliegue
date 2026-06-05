package com.icesi.bu_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.icesi.bu_app.model.Place;
import com.icesi.bu_app.model.Status;
import com.icesi.bu_app.repository.IPlaceRepository;
import com.icesi.bu_app.repository.IStatusRepository;
import com.icesi.bu_app.service.IPlaceService;
import com.icesi.bu_app.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService implements IPlaceService {

    private final IPlaceRepository placeRepository;
    private final IStatusRepository statusRepository;

    @Override
    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    @Override
    public Page<Place> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        return placeRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    @Override
    public Place findById(Integer id) {
        return placeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Place not found"));
    }

    @Override
    public Place save(Place place) {
        if (place.getStatus() != null) {
            place.setStatus(resolveStatus(place.getStatus()));
        }
        return placeRepository.save(place);
    }

    @Override
    public Place update(Integer id, Place place) {
        Place foundPlace = findById(id);
        foundPlace.setName(place.getName());
        foundPlace.setStatus(place.getStatus() != null ? resolveStatus(place.getStatus()) : null);
        return placeRepository.save(foundPlace);
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        placeRepository.deleteById(id);
    }

    private Status resolveStatus(Status status) {
        if (status.getId() == null) {
            throw new ResourceNotFoundException("Status not found");
        }
        return statusRepository.findById(status.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
    }
}