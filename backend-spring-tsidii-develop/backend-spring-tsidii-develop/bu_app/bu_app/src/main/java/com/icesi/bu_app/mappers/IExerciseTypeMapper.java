package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.icesi.bu_app.controller.rest.dto.ExerciseTypeRequest;
import com.icesi.bu_app.controller.rest.dto.ExerciseTypeResponse;
import com.icesi.bu_app.model.ExerciseType;

@Mapper(componentModel = "spring")
public interface IExerciseTypeMapper {
    ExerciseTypeResponse toResponse(ExerciseType exerciseType);

    List<ExerciseTypeResponse> toResponseList(List<ExerciseType> exerciseTypes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercises", ignore = true)
    ExerciseType toEntity(ExerciseTypeRequest request);
}
