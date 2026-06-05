package com.icesi.bu_app.controller.rest.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class EventPlaceRequest {
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer eventId;
    private Integer placeId;
}
