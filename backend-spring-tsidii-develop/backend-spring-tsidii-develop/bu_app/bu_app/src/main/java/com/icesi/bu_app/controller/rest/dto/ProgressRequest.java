package com.icesi.bu_app.controller.rest.dto;

import lombok.Data;

@Data
public class ProgressRequest {
    private Integer repetitions;
    private String time;
    private Integer stressLevelId;
    private Integer progressTypeId;
    private Integer traineeId;
    private Integer routineId; 
    private String performedAt; 
}
