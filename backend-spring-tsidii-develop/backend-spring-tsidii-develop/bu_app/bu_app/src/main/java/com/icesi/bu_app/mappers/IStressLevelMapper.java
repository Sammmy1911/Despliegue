package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.StressLevelRequest;
import com.icesi.bu_app.controller.rest.dto.StressLevelResponse;
import com.icesi.bu_app.model.StressLevel;

@Mapper(componentModel = "spring")
public interface IStressLevelMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "progresses", ignore = true)
    })
    StressLevel stressLevelRequestToStressLevel(StressLevelRequest stressLevelRequest);
    
    StressLevelResponse stressLevelToStressLevelResponse(StressLevel stressLevel);

    List<StressLevelResponse> stressLevelsToStressLevelResponses(List<StressLevel> stressLevels);

    default Page<StressLevelResponse> stressLevelsToStressLevelResponses(Page<StressLevel> stressLevels) {
        return stressLevels.map(
            this::stressLevelToStressLevelResponse
        );
    }
}
