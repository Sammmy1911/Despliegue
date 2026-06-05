package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer managerCode;
    private String managerName;
}
