package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.StatusRequest;
import com.icesi.bu_app.controller.rest.dto.StatusResponse;
import com.icesi.bu_app.model.Status;

@Mapper(componentModel = "spring")
public interface IStatusMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "place", ignore = true)
    })
    Status statusRequestToStatus(StatusRequest statusRequest);

    StatusResponse statusToStatusResponse(Status status);

    List<StatusResponse> statusesToStatusResponses(List<Status> statuses);

    default Page<StatusResponse> statusesToStatusResponses(Page<Status> statuses){
        return statuses.map(this::statusToStatusResponse);
    }
}
