package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.EventPlaceRequest;
import com.icesi.bu_app.controller.rest.dto.EventPlaceResponse;
import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.EventPlace;
import com.icesi.bu_app.model.Place;

@Mapper(componentModel = "spring")
public interface IEventPlaceMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "event", source = "eventId", qualifiedByName = "eventFromId"),
        @Mapping(target = "place", source = "placeId", qualifiedByName = "placeFromId")
    })
    EventPlace eventPlaceRequestToEventPlace(EventPlaceRequest request);

    @Mappings({
        @Mapping(source = "event.id", target = "eventId"),
        @Mapping(source = "event.name", target = "eventName"),
        @Mapping(source = "place.id", target = "placeId"),
        @Mapping(source = "place.name", target = "placeName")
    })
    EventPlaceResponse eventPlaceToEventPlaceResponse(EventPlace eventPlace);

    List<EventPlaceResponse> eventPlacesToEventPlaceResponses(List<EventPlace> eps);

    default Page<EventPlaceResponse> eventPlacesToEventPlaceResponses(Page<EventPlace> eps){
        return eps.map(this::eventPlaceToEventPlaceResponse);
    }

    @Named("eventFromId")
    default Event createEvent(Integer eventId) {
        if (eventId == null) return null;
        Event event = new Event();
        event.setId(eventId);
        return event;
    }

    @Named("placeFromId")
    default Place createPlace(Integer placeId) {
        if (placeId == null) return null;
        Place place = new Place();
        place.setId(placeId);
        return place;
    }
}
