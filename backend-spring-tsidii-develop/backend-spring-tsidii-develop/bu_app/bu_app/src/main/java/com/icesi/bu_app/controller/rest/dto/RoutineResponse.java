package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutineResponse {
    private Integer id;
    private String name;
    private Integer trainerCode;
    private String trainerName;
    private Integer traineeCode;
    private String traineeName;
}
