package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.icesi.bu_app.controller.rest.dto.RoutineExerciseRequest;
import com.icesi.bu_app.controller.rest.dto.RoutineExerciseResponse;
import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.RoutineExercise;

@Mapper(componentModel = "spring")
public interface IRoutineExerciseMapper{

    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    @Mapping(target = "routineId", source = "routine.id")
    @Mapping(target = "routineName", source = "routine.name")
    RoutineExerciseResponse toResponse(RoutineExercise routineExercise);

    List<RoutineExerciseResponse> toResponseList(List<RoutineExercise> routineExercises);

    @Mapping(target = "exercise", source = "exerciseId", qualifiedByName = "exerciseFromId")
    @Mapping(target = "routine", source = "routineId", qualifiedByName = "routineFromId")
    @Mapping(target = "id", ignore = true)
    RoutineExercise toEntity(RoutineExerciseRequest request);

    @Named("exerciseFromId")
    default Exercise exerciseFromId(Integer id) {
        if (id == null) {
            return null;
        }

        Exercise exercise = new Exercise();
        exercise.setId(id);
        return exercise;
    }

    @Named("routineFromId")
    default Routine routineFromId(Integer id) {
        if (id == null) {
            return null;
        }

        Routine routine = new Routine();
        routine.setId(id);
        return routine;
    }
}
