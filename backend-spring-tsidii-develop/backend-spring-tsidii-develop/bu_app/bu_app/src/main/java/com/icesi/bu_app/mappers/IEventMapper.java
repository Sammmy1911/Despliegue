package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.EventRequest;
import com.icesi.bu_app.controller.rest.dto.EventResponse;
import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring", uses = {IUserMapper.class})
public interface IEventMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "manager", source = "managerId", qualifiedByName = "userFromId"),
        @Mapping(target = "eventsPlaces", ignore = true)
    })
    Event eventRequestToEvent(EventRequest eventRequest);

    @Mappings({
        @Mapping(source = "manager.code", target = "managerCode"),
        @Mapping(source = "manager.name", target = "managerName")
    })
    EventResponse eventToEventResponse(Event event);

    List<EventResponse> eventsToEventResponses(List<Event> events);

    default Page<EventResponse> eventsToEventResponses(Page<Event> events){
        return events.map(this::eventToEventResponse);
    }

    @Named("userFromId")
    default User createManager(Integer managerId) {
        if (managerId == null) return null;
        User user = new User();
        user.setCode(managerId);
        return user;
    }
}
