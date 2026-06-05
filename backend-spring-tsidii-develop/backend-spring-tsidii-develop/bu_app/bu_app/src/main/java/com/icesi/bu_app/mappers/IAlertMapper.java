package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.AlertRequest;
import com.icesi.bu_app.controller.rest.dto.AlertResponse;
import com.icesi.bu_app.model.Alert;
import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring", uses = {IUserMapper.class})
public interface IAlertMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "trainer", source = "trainerId", qualifiedByName = "userFromId"),
        @Mapping(target = "trainee", source = "traineeId", qualifiedByName = "userFromId")
    })
    Alert alertRequestToAlert(AlertRequest alertRequest);

    @Mappings({
        @Mapping(source = "trainer.code", target = "trainerCode"),
        @Mapping(source = "trainer.name", target = "trainerName"),
        @Mapping(source = "trainee.code", target = "traineeCode"),
        @Mapping(source = "trainee.name", target = "traineeName")
    })
    AlertResponse alertToAlertResponse(Alert alert);

    List<AlertResponse> alertsToAlertResponses(List<Alert> alerts);

    default Page<AlertResponse> alertsToAlertResponses(Page<Alert> alerts){
        return alerts.map(this::alertToAlertResponse);
    }

    @Named("userFromId")
    default User userFromId(Integer userId) {
        if (userId == null) return null;
        User user = new User();
        user.setCode(userId);
        return user;
    }
}
