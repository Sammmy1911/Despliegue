package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.icesi.bu_app.controller.rest.dto.ExerciseDifficultyRequest;
import com.icesi.bu_app.controller.rest.dto.ExerciseDifficultyResponse;
import com.icesi.bu_app.model.ExerciseDifficulty;

@Mapper(componentModel = "spring")
public interface IExerciseDifficultyMapper {
    ExerciseDifficultyResponse toResponse(ExerciseDifficulty exerciseDifficulty);

    List<ExerciseDifficultyResponse> toResponseList(List<ExerciseDifficulty> exerciseDifficulties);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercises", ignore = true)
    ExerciseDifficulty toEntity(ExerciseDifficultyRequest request);
}
