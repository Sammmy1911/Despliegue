package com.icesi.bu_app.controller.rest.dto;

import lombok.Data;

@Data
public class RecommendationRequest {
    
    private String description;
    private Integer trainerId;
    private Integer progressId;

}
