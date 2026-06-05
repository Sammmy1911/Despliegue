package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.icesi.bu_app.controller.rest.dto.RoutineRequest;
import com.icesi.bu_app.controller.rest.dto.RoutineResponse;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring")
public interface IRoutineMapper {

    @Mapping(target = "trainerCode", source = "trainer.code")
    @Mapping(target = "trainerName", source = "trainer.name")
    @Mapping(target = "traineeCode", source = "trainee.code")
    @Mapping(target = "traineeName", source = "trainee.name")
    RoutineResponse toResponse(Routine routine);

    List<RoutineResponse> toResponseList(List<Routine> routines);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainer", source = "trainerCode", qualifiedByName = "userFromCode")
    @Mapping(target = "trainee", source = "traineeCode", qualifiedByName = "userFromCode")
    @Mapping(target = "routinesExercises", ignore = true)
    @Mapping(target = "progresses", ignore = true)
    Routine toEntity(RoutineRequest request);

    @Named("userFromCode")
    default User userFromCode(Integer code) {
        if (code == null) {
            return null;
        }

        User user = new User();
        user.setCode(code);
        return user;
    }
}
