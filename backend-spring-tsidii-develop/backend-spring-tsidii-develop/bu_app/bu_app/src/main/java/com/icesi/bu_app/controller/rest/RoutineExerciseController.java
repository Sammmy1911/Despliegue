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

import com.icesi.bu_app.controller.rest.dto.RoutineExerciseRequest;
import com.icesi.bu_app.controller.rest.dto.RoutineExerciseResponse;
import com.icesi.bu_app.mappers.IRoutineExerciseMapper;
import com.icesi.bu_app.model.RoutineExercise;
import com.icesi.bu_app.service.IRoutineExerciseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/routine-exercises")
@Tag(name = "Rutina-Ejercicio", description = "Operaciones sobre la relación rutina-ejercicio")
public class RoutineExerciseController {

	private final IRoutineExerciseService routineExerciseService;
	private final IRoutineExerciseMapper routineExerciseMapper;

	@Operation(summary = "Listar todas las relaciones rutina-ejercicio", description = "Retorna todas las relaciones registradas")
	@ApiResponse(responseCode = "200", description = "Listado de relaciones")
	@GetMapping
	public ResponseEntity<List<RoutineExerciseResponse>> getAll() {
		List<RoutineExerciseResponse> response = routineExerciseMapper.toResponseList(routineExerciseService.findAll());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Obtener relación por id", description = "Retorna una relación específica por su identificador")
	@ApiResponse(responseCode = "200", description = "Relación encontrada")
	@GetMapping("/{id}")
	public ResponseEntity<RoutineExerciseResponse> getById(
			@Parameter(description = "ID de la relación") @PathVariable Integer id) {
		return ResponseEntity.ok(routineExerciseMapper.toResponse(routineExerciseService.findById(id)));
	}

	@Operation(summary = "Crear relación", description = "Crea una nueva relación rutina-ejercicio")
	@ApiResponse(responseCode = "201", description = "Relación creada")
	@PreAuthorize("@permissionService.canManageRoutineExercise(authentication, #request.routineId)")
	@PostMapping
	public ResponseEntity<RoutineExerciseResponse> create(@RequestBody RoutineExerciseRequest request) {
		RoutineExercise saved = routineExerciseService.save(routineExerciseMapper.toEntity(request));
		RoutineExerciseResponse response = routineExerciseMapper.toResponse(saved);
		return ResponseEntity.created(URI.create("/api/routine-exercises/" + response.getId())).body(response);
	}

	@Operation(summary = "Actualizar relación", description = "Actualiza una relación existente por id")
	@ApiResponse(responseCode = "200", description = "Relación actualizada")
	@PreAuthorize("@permissionService.canModifyRoutineExercise(authentication, #id)")
	@PutMapping("/{id}")
	public ResponseEntity<RoutineExerciseResponse> update(
			@Parameter(description = "ID de la relación") @PathVariable Integer id,
			@RequestBody RoutineExerciseRequest request) {
		RoutineExercise updated = routineExerciseService.update(id, routineExerciseMapper.toEntity(request));
		return ResponseEntity.ok(routineExerciseMapper.toResponse(updated));
	}

	@Operation(summary = "Eliminar relación", description = "Elimina una relación existente por id")
	@ApiResponse(responseCode = "204", description = "Relación eliminada")
	@PreAuthorize("@permissionService.canModifyRoutineExercise(authentication, #id)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "ID de la relación") @PathVariable Integer id) {
		routineExerciseService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
