package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressAggregationResponse {
    private String period;
    private Integer totalRepetitions;
    private Integer entriesCount;
}
