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

import com.icesi.bu_app.controller.rest.dto.ExerciseTypeRequest;
import com.icesi.bu_app.controller.rest.dto.ExerciseTypeResponse;
import com.icesi.bu_app.mappers.IExerciseTypeMapper;
import com.icesi.bu_app.model.ExerciseType;
import com.icesi.bu_app.service.IExerciseTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/exercise-types")
@Tag(name = "Tipos de ejercicio", description = "Operaciones sobre tipos de ejercicios")
public class ExerciseTypeController {

	private final IExerciseTypeService exerciseTypeService;
	private final IExerciseTypeMapper exerciseTypeMapper;

	@Operation(summary = "Listar todos los tipos", description = "Retorna todos los tipos de ejercicio registrados")
	@ApiResponse(responseCode = "200", description = "Listado de tipos")
	@GetMapping
	public ResponseEntity<List<ExerciseTypeResponse>> getAll() {
		List<ExerciseTypeResponse> response = exerciseTypeMapper.toResponseList(exerciseTypeService.findAll());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Obtener tipo por id", description = "Retorna un tipo específico por su identificador")
	@ApiResponse(responseCode = "200", description = "Tipo encontrado")
	@GetMapping("/{id}")
	public ResponseEntity<ExerciseTypeResponse> getById(
			@Parameter(description = "ID del tipo") @PathVariable Integer id) {
		return ResponseEntity.ok(exerciseTypeMapper.toResponse(exerciseTypeService.findById(id)));
	}

	@Operation(summary = "Crear tipo", description = "Crea un nuevo tipo de ejercicio")
	@ApiResponse(responseCode = "201", description = "Tipo creado")
	@PostMapping
	public ResponseEntity<ExerciseTypeResponse> create(@RequestBody ExerciseTypeRequest request) {
		ExerciseType saved = exerciseTypeService.save(exerciseTypeMapper.toEntity(request));
		ExerciseTypeResponse response = exerciseTypeMapper.toResponse(saved);
		return ResponseEntity.created(URI.create("/api/exercise-types/" + response.getId())).body(response);
	}

	@Operation(summary = "Actualizar tipo", description = "Actualiza un tipo existente por id")
	@ApiResponse(responseCode = "200", description = "Tipo actualizado")
	@PutMapping("/{id}")
	public ResponseEntity<ExerciseTypeResponse> update(@Parameter(description = "ID del tipo") @PathVariable Integer id,
			@RequestBody ExerciseTypeRequest request) {
		ExerciseType updated = exerciseTypeService.update(id, exerciseTypeMapper.toEntity(request));
		return ResponseEntity.ok(exerciseTypeMapper.toResponse(updated));
	}

	@Operation(summary = "Eliminar tipo", description = "Elimina un tipo existente por id")
	@ApiResponse(responseCode = "204", description = "Tipo eliminado")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "ID del tipo") @PathVariable Integer id) {
		exerciseTypeService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
