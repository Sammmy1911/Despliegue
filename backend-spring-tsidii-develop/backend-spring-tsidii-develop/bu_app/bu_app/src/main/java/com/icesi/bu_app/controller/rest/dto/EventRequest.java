package com.icesi.bu_app.controller.rest.dto;

import lombok.Data;

@Data
public class EventRequest {
    private String name;
    private String description;
    private Integer managerId;
}
