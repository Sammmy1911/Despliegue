package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
    private Integer id;
    private String description;
    private Integer trainerCode;
    private String trainerName;
    private String trainerEmail;
    private Integer progressId;
}
