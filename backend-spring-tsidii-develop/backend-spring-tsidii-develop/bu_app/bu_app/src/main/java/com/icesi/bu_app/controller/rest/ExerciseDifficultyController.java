package com.icesi.bu_app.controller.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icesi.bu_app.controller.rest.dto.ExerciseDifficultyRequest;
import com.icesi.bu_app.controller.rest.dto.ExerciseDifficultyResponse;
import com.icesi.bu_app.mappers.IExerciseDifficultyMapper;
import com.icesi.bu_app.model.ExerciseDifficulty;
import com.icesi.bu_app.service.IExerciseDifficultyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/exercise-difficulties")
@Tag(name = "Dificultades de ejercicio", description = "Operaciones sobre dificultades de ejercicios")
public class ExerciseDifficultyController {

	private final IExerciseDifficultyService exerciseDifficultyService;
	private final IExerciseDifficultyMapper exerciseDifficultyMapper;

	@Operation(summary = "Listar todas las dificultades", description = "Retorna todas las dificultades de ejercicio registradas")
	@ApiResponse(responseCode = "200", description = "Listado de dificultades")
	@GetMapping
	public ResponseEntity<List<ExerciseDifficultyResponse>> getAll() {
		List<ExerciseDifficultyResponse> response = exerciseDifficultyMapper
				.toResponseList(exerciseDifficultyService.findAll());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Obtener dificultad por id", description = "Retorna una dificultad específica por su identificador")
	@ApiResponse(responseCode = "200", description = "Dificultad encontrada")
	@GetMapping("/{id}")
	public ResponseEntity<ExerciseDifficultyResponse> getById(
			@Parameter(description = "ID de la dificultad") @PathVariable Integer id) {
		return ResponseEntity.ok(exerciseDifficultyMapper.toResponse(exerciseDifficultyService.findById(id)));
	}

	@Operation(summary = "Crear dificultad", description = "Crea una nueva dificultad de ejercicio")
	@ApiResponse(responseCode = "201", description = "Dificultad creada")
	@PostMapping
	public ResponseEntity<ExerciseDifficultyResponse> create(@RequestBody ExerciseDifficultyRequest request) {
		ExerciseDifficulty saved = exerciseDifficultyService.save(exerciseDifficultyMapper.toEntity(request));
		ExerciseDifficultyResponse response = exerciseDifficultyMapper.toResponse(saved);
		return ResponseEntity.created(URI.create("/api/exercise-difficulties/" + response.getId()))
				.body(response);
	}

	@Operation(summary = "Actualizar dificultad", description = "Actualiza una dificultad existente por id")
	@ApiResponse(responseCode = "200", description = "Dificultad actualizada")
	@PutMapping("/{id}")
	public ResponseEntity<ExerciseDifficultyResponse> update(
			@Parameter(description = "ID de la dificultad") @PathVariable Integer id,
			@RequestBody ExerciseDifficultyRequest request) {
		ExerciseDifficulty updated = exerciseDifficultyService.update(id, exerciseDifficultyMapper.toEntity(request));
		return ResponseEntity.ok(exerciseDifficultyMapper.toResponse(updated));
	}

	@Operation(summary = "Eliminar dificultad", description = "Elimina una dificultad existente por id")
	@ApiResponse(responseCode = "204", description = "Dificultad eliminada")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "ID de la dificultad") @PathVariable Integer id) {
		exerciseDifficultyService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
