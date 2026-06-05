package com.icesi.bu_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.Place;

@Repository
public interface IPlaceRepository extends JpaRepository<Place, Integer> {
    
}
