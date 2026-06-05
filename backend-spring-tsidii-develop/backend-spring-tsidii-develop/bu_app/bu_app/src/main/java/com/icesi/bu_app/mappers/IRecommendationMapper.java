package com.icesi.bu_app.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.icesi.bu_app.controller.rest.dto.RecommendationRequest;
import com.icesi.bu_app.controller.rest.dto.RecommendationResponse;
import com.icesi.bu_app.model.Progress;
import com.icesi.bu_app.model.Recommendation;
import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring", uses = {IUserMapper.class})
public interface IRecommendationMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "trainer", source = "trainerId", qualifiedByName = "userFromId"),
        @Mapping(target = "progress", source = "progressId", qualifiedByName = "progressFromId")
    })
    Recommendation recommendationRequestToRecommendation(RecommendationRequest recommendationRequest);
    
    @Mappings({
        @Mapping(source = "trainer.code", target = "trainerCode"),
        @Mapping(source = "trainer.name", target = "trainerName"),
        @Mapping(source = "trainer.email", target = "trainerEmail"),
        @Mapping(source = "progress.id", target = "progressId")
    })
    RecommendationResponse recommendationToRecommendationResponse(Recommendation recommendation);

    List<RecommendationResponse> recommendationsToRecommendationResponses(List<Recommendation> recommendations);

    default Page<RecommendationResponse> recommendationsToRecommendationResponses(Page<Recommendation> recommendations){
        return recommendations.map(
            this::recommendationToRecommendationResponse
        );
    }

    @Named("userFromId")
    default User userFromId(Integer userId) {
        if (userId == null) return null;
        User user = new User();
        user.setCode(userId);
        return user;
    }

    @Named("progressFromId")
    default Progress progressFromId(Integer progressId) {
        if (progressId == null) return null;
        Progress progress = new Progress();
        progress.setId(progressId);
        return progress;
    }
}
