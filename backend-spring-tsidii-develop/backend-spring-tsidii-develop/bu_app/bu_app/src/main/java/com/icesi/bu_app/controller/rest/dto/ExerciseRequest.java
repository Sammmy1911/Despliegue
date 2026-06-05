package com.icesi.bu_app.controller.rest.dto;

import lombok.Data;

@Data
public class ExerciseRequest {
	private String name;
	private Integer length;
	private String description;
	private byte[] video;
	private Boolean custom;
	private Integer difficultyId;
	private Integer typeId;
	private Integer ownerCode;
}
