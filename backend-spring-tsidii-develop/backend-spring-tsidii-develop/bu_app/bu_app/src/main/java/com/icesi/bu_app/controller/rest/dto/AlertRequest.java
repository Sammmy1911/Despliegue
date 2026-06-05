package com.icesi.bu_app.controller.rest.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AlertRequest {
    private String message;
    private Timestamp sendDate;
    private Integer trainerId;
    private Integer traineeId;
}
