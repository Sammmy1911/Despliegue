package com.icesi.bu_app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.icesi.bu_app.model.Event;

@Repository
public interface IEventRepository extends JpaRepository<Event, Integer> {
    // Buscar eventos por nombre
    List<Event> findByName(String name);

    List<Event> findByManagerCode(Integer managerCode);

    List<Event> findByEventsPlacesPlaceId(Integer placeId);
}
