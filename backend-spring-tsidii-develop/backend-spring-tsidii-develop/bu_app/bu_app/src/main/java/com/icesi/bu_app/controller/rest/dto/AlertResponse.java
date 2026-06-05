package com.icesi.bu_app.controller.rest.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertResponse {
    private Integer id;
    private String message;
    private Timestamp sendDate;
    private Integer trainerCode;
    private String trainerName;
    private Integer traineeCode;
    private String traineeName;
}
