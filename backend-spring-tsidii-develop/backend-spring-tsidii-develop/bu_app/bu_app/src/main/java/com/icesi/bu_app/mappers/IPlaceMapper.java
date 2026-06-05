package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.PlaceRequest;
import com.icesi.bu_app.controller.rest.dto.PlaceResponse;
import com.icesi.bu_app.model.Place;
import com.icesi.bu_app.model.Status;

@Mapper(componentModel = "spring", uses = {IStatusMapper.class})
public interface IPlaceMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "status", source = "statusId", qualifiedByName = "statusFromId"),
        @Mapping(target = "eventsPlaces", ignore = true)
    })
    Place placeRequestToPlace(PlaceRequest placeRequest);

    @Mappings({
        @Mapping(source = "status.id", target = "statusId"),
        @Mapping(source = "status.status", target = "statusName")
    })
    PlaceResponse placeToPlaceResponse(Place place);

    List<PlaceResponse> placesToPlaceResponses(List<Place> places);

    default Page<PlaceResponse> placesToPlaceResponses(Page<Place> places){
        return places.map(this::placeToPlaceResponse);
    }

    @Named("statusFromId")
    default Status statusFromId(Integer statusId) {
        if (statusId == null) return null;
        Status status = new Status();
        status.setId(statusId);
        return status;
    }
}
