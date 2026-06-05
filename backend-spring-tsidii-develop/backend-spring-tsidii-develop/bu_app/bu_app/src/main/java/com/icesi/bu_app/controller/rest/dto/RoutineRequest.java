package com.icesi.bu_app.controller.rest.dto;

import lombok.Data;

@Data
public class RoutineRequest {
    private String name;
    private Integer trainerCode;
    private Integer traineeCode;
}
