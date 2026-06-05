package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.ProgressTypeRequest;
import com.icesi.bu_app.controller.rest.dto.ProgressTypeResponse;
import com.icesi.bu_app.model.ProgressType;

@Mapper(componentModel = "spring")
public interface IProgressTypeMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "progresses", ignore = true)
    })
    ProgressType progressTypeRequestToProgressType(ProgressTypeRequest progressTypeRequest);

    ProgressTypeResponse progressTypeToProgressTypeResponse(ProgressType progressType);

    List<ProgressTypeResponse> progressTypesToProgressTypeResponses(List<ProgressType> progressTypes);

    default Page<ProgressTypeResponse> progressTypesToProgressTypeResponses(Page<ProgressType> progressTypes) {
        return progressTypes.map(
            this::progressTypeToProgressTypeResponse
        );
    }
}
