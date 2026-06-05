package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.ProgressRequest;
import com.icesi.bu_app.controller.rest.dto.ProgressResponse;
import com.icesi.bu_app.model.Progress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import com.icesi.bu_app.model.ProgressType;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.model.StressLevel;
import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring", uses = {IUserMapper.class, IProgressTypeMapper.class, IStressLevelMapper.class, IRoutineMapper.class})
public interface IProgressMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "stressLevel", source = "stressLevelId", qualifiedByName = "stressLevelFromId"),
        @Mapping(target = "progressType", source = "progressTypeId", qualifiedByName = "progressTypeFromId"),
        @Mapping(target = "trainee", source = "traineeId", qualifiedByName = "userFromId"),
        @Mapping(target = "routine", source = "routineId", qualifiedByName = "routineFromId"),
        @Mapping(target = "performedAt", source = "performedAt", qualifiedByName = "stringToTimestamp"),
        @Mapping(target = "recommendations", ignore = true)
    })
    Progress progressRequestToProgress(ProgressRequest progressRequest);

    @Mappings({
        @Mapping(source = "stressLevel.id", target = "stressLevelId"),
        @Mapping(source = "stressLevel.level", target = "stressLevelName"),
        @Mapping(source = "progressType.id", target = "progressTypeId"),
        @Mapping(source = "progressType.type", target = "progressTypeName"),
        @Mapping(source = "trainee.code", target = "traineeId"),
        @Mapping(source = "trainee.name", target = "traineeName"),
        @Mapping(source = "routine.id", target = "routineId"),
        @Mapping(source = "routine.name", target = "routineName"),
        @Mapping(source = "performedAt", target = "performedAt", qualifiedByName = "timestampToString")
    })
    ProgressResponse progressToProgressResponse(Progress progress);

    List<ProgressResponse> progressesToProgressResponses(List<Progress> progresses);

    default Page<ProgressResponse> progressesToProgressResponses(Page<Progress> progresses){
        return progresses.map(
            this::progressToProgressResponse
        );
    }

    @Named("stressLevelFromId")
    default StressLevel createStressLevel(Integer stressLevelId) {
        if (stressLevelId == null) return null;
        StressLevel stressLevel = new StressLevel();
        stressLevel.setId(stressLevelId);
        return stressLevel;
    }

    @Named("progressTypeFromId")
    default ProgressType createProgressType(Integer progressTypeId) {
        if (progressTypeId == null) return null;
        ProgressType progressType = new ProgressType();
        progressType.setId(progressTypeId);
        return progressType;
    }

    @Named("userFromId")
    default User createUser(Integer userId) {
        if (userId == null) return null;
        User user = new User();
        user.setCode(userId);
        return user;
    }

    @Named("routineFromId")
    default Routine createRoutine(Integer routineId) {
        if (routineId == null) return null;
        Routine routine = new Routine();
        routine.setId(routineId);
        return routine;
    }

    @Named("stringToTimestamp")
    default Timestamp stringToTimestamp(String s) {
        if (s == null) return null;
        LocalDateTime ldt = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return Timestamp.valueOf(ldt);
    }

    @Named("timestampToString")
    default String timestampToString(Timestamp ts) {
        if (ts == null) return null;
        return ts.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
