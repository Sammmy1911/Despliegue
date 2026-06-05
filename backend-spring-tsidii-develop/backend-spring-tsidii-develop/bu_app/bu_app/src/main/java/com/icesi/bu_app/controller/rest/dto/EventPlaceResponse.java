package com.icesi.bu_app.controller.rest.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPlaceResponse {
    private Integer id;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer eventId;
    private String eventName;
    private Integer placeId;
    private String placeName;
}
