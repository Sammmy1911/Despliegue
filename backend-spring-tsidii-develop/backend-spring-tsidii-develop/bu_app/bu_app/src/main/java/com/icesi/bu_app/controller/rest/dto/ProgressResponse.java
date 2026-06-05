package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressResponse {
    private Integer id;
    private Integer repetitions;
    private String time;
    private String performedAt;
    private Integer stressLevelId;
    private String stressLevelName;
    private Integer progressTypeId;
    private String progressTypeName;
    private Integer traineeId;
    private String traineeName;
    private Integer routineId;
    private String routineName;
}
