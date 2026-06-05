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

import com.icesi.bu_app.controller.rest.dto.RoutineRequest;
import com.icesi.bu_app.controller.rest.dto.RoutineResponse;
import com.icesi.bu_app.mappers.IRoutineMapper;
import com.icesi.bu_app.model.Routine;
import com.icesi.bu_app.service.IRoutineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/routines")
@Tag(name = "Rutinas", description = "Operaciones sobre rutinas")
public class RoutineController {

	private final IRoutineService routineService;
	private final IRoutineMapper routineMapper;

	@Operation(summary = "Listar todas las rutinas", description = "Retorna todas las rutinas registradas.")
	@ApiResponse(responseCode = "200", description = "Listado de rutinas")
	@PreAuthorize("@permissionService.isAdmin(authentication)")
	@GetMapping
	public ResponseEntity<List<RoutineResponse>> getAll() {
		List<RoutineResponse> response = routineMapper.toResponseList(routineService.findAll());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Obtener rutina por id", description = "Retorna una rutina específica por su identificador.")
	@ApiResponse(responseCode = "200", description = "Rutina encontrada")
	@PreAuthorize("@permissionService.canViewRoutine(authentication, #id)")
	@GetMapping("/{id}")
	public ResponseEntity<RoutineResponse> getById(
			@Parameter(description = "ID de la rutina") @PathVariable Integer id) {
		return ResponseEntity.ok(routineMapper.toResponse(routineService.findById(id)));
	}

	@Operation(summary = "Crear rutina", description = "Crea una rutina con la información suministrada.")
	@ApiResponse(responseCode = "201", description = "Rutina creada")
	@PreAuthorize("@permissionService.canManageRoutine(authentication, #request.trainerCode, #request.traineeCode)")
	@PostMapping
	public ResponseEntity<RoutineResponse> create(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la rutina a crear") @RequestBody RoutineRequest request) {
		Routine saved = routineService.save(routineMapper.toEntity(request));
		RoutineResponse response = routineMapper.toResponse(saved);
		return ResponseEntity.created(URI.create("/api/routines/" + response.getId())).body(response);
	}

	@Operation(summary = "Actualizar rutina", description = "Actualiza una rutina existente por id.")
	@ApiResponse(responseCode = "200", description = "Rutina actualizada")
	@PreAuthorize("@permissionService.canModifyRoutine(authentication, #id) or @permissionService.isAdmin(authentication)")
	@PutMapping("/{id}")
	public ResponseEntity<RoutineResponse> update(
			@Parameter(description = "ID de la rutina") @PathVariable Integer id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la rutina a actualizar") @RequestBody RoutineRequest request) {
		Routine updated = routineService.update(id, routineMapper.toEntity(request));
		return ResponseEntity.ok(routineMapper.toResponse(updated));
	}

	@Operation(summary = "Eliminar rutina", description = "Elimina una rutina existente por id.")
	@ApiResponse(responseCode = "204", description = "Rutina eliminada")
	@PreAuthorize("@permissionService.canModifyRoutine(authentication, #id)")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "ID de la rutina") @PathVariable Integer id) {
		routineService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Listar rutinas por trainer", description = "Retorna las rutinas creadas/asignadas por un trainer")
	@ApiResponse(responseCode = "200", description = "Listado de rutinas del trainer")
	@PreAuthorize("@permissionService.canAccessTrainer(authentication, #trainerId)")
	@GetMapping("/trainer/{trainerId}")
	public ResponseEntity<List<RoutineResponse>> getByTrainer(
			@Parameter(description = "ID del trainer") @PathVariable Integer trainerId) {
		return ResponseEntity.ok(routineMapper.toResponseList(routineService.findByTrainerCode(trainerId)));
	}

	@Operation(summary = "Listar rutinas por trainee", description = "Retorna las rutinas asignadas a un trainee. Trainers solo pueden ver sus trainees.")
	@ApiResponse(responseCode = "200", description = "Listado de rutinas del trainee")
	@PreAuthorize("@permissionService.canViewTrainee(authentication, #traineeId) or @permissionService.isAdmin(authentication)")
	@GetMapping("/trainee/{traineeId}")
	public ResponseEntity<List<RoutineResponse>> getByTrainee(
			@Parameter(description = "ID del trainee") @PathVariable Integer traineeId) {
		return ResponseEntity.ok(routineMapper.toResponseList(routineService.findByTraineeCode(traineeId)));
	}
}
