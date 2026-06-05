package com.icesi.bu_app.mappers;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.icesi.bu_app.controller.rest.dto.ExerciseRequest;
import com.icesi.bu_app.controller.rest.dto.ExerciseResponse;
import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.model.ExerciseDifficulty;
import com.icesi.bu_app.model.ExerciseType;
import com.icesi.bu_app.model.User;

@Mapper(componentModel = "spring")
public interface IExerciseMapper {

	@Mapping(target = "difficultyId", source = "difficulty.id")
	@Mapping(target = "difficultyName", source = "difficulty.name")
	@Mapping(target = "typeId", source = "type.id")
	@Mapping(target = "typeName", source = "type.name")
	@Mapping(target = "ownerCode", source = "owner.code")
	@Mapping(target = "ownerName", source = "owner.name")
	@Mapping(target = "custom", source = "custom")
	@Mapping(target = "video", source = "video", qualifiedByName = "blobToBytes")
	ExerciseResponse toResponse(Exercise exercise);

	List<ExerciseResponse> toResponseList(List<Exercise> exercises);

	@Mapping(target = "difficulty", source = "difficultyId", qualifiedByName = "difficultyFromId")
	@Mapping(target = "type", source = "typeId", qualifiedByName = "typeFromId")
	@Mapping(target = "owner", source = "ownerCode", qualifiedByName = "userFromCode")
	@Mapping(target = "routinesExercises", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "custom", source = "custom")
	@Mapping(target = "video", source = "video", qualifiedByName = "bytesToBlob")
	Exercise toEntity(ExerciseRequest request);

	@Named("difficultyFromId")
	default ExerciseDifficulty difficultyFromId(Integer id) {
		if (id == null) {
			return null;
		}

		ExerciseDifficulty difficulty = new ExerciseDifficulty();
		difficulty.setId(id);
		return difficulty;
	}

	@Named("typeFromId")
	default ExerciseType typeFromId(Integer id) {
		if (id == null) {
			return null;
		}

		ExerciseType type = new ExerciseType();
		type.setId(id);
		return type;
	}

	@Named("userFromCode")
	default User userFromCode(Integer code) {
		if (code == null) {
			return null;
		}

		User user = new User();
		user.setCode(code);
		return user;
	}

	@Named("blobToBytes")
	default byte[] blobToBytes(Blob blob) {
		if (blob == null) {
			return null;
		}

		try {
			return blob.getBytes(1, (int) blob.length());
		} catch (SQLException e) {
			throw new IllegalArgumentException("Unable to read exercise video", e);
		}
	}

	@Named("bytesToBlob")
	default Blob bytesToBlob(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		try {
			return new SerialBlob(bytes);
		} catch (SQLException e) {
			throw new IllegalArgumentException("Unable to convert exercise video", e);
		}
	}
}
