package com.icesi.bu_app.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponse {
	private Integer id;
	private String name;
	private Integer length;
	private String description;
	private byte[] video;
	private Boolean custom;
	private Integer difficultyId;
	private String difficultyName;
	private Integer typeId;
	private String typeName;
	private Integer ownerCode;
	private String ownerName;
}
