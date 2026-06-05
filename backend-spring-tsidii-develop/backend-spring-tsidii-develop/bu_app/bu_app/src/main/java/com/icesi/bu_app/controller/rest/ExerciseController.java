package com.icesi.bu_app.controller.rest;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icesi.bu_app.controller.rest.dto.ExerciseRequest;
import com.icesi.bu_app.controller.rest.dto.ExerciseResponse;
import com.icesi.bu_app.mappers.IExerciseMapper;
import com.icesi.bu_app.model.Exercise;
import com.icesi.bu_app.service.IExerciseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/exercises")
@Tag(name = "Ejercicios", description = "Operaciones sobre ejercicios")
public class ExerciseController {
    private final IExerciseService exerciseService;
    private final IExerciseMapper exerciseMapper;

    @Operation(summary = "Listar ejercicios", description = "Retorna todos los ejercicios registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de ejercicios", useReturnTypeSchema = true)
    })
    @GetMapping
        public ResponseEntity<List<ExerciseResponse>> getAll(
                        @RequestParam(required = false) Boolean custom) {
                List<ExerciseResponse> exercises;

                if (custom == null) {
                        exercises = exerciseMapper.toResponseList(exerciseService.findAll());
                } else if (custom) {
                        exercises = exerciseMapper.toResponseList(exerciseService.findAllCustom());
                } else {
                        exercises = exerciseMapper.toResponseList(exerciseService.findAllPredefined());
                }

        return ResponseEntity.ok(exercises);
    }

    @Operation(summary = "Obtener ejercicio por id", description = "Retorna un ejercicio específico por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicio encontrado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(
            @Parameter(description = "ID del ejercicio") @PathVariable Integer id) {
        ExerciseResponse response = exerciseMapper.toResponse(exerciseService.findById(id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear ejercicio", description = "Crea un nuevo ejercicio con la información suministrada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ejercicio creado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PreAuthorize("@permissionService.canManageExercise(authentication, #request.ownerCode, #request.custom)")
    @PostMapping
    public ResponseEntity<ExerciseResponse> create(
            @Parameter(description = "Datos del ejercicio a crear") @RequestBody ExerciseRequest request) {
        Exercise savedExercise = exerciseService.save(exerciseMapper.toEntity(request));
        ExerciseResponse response = exerciseMapper.toResponse(savedExercise);
        return ResponseEntity.created(URI.create("/api/exercises/" + response.getId())).body(response);
    }

    @Operation(summary = "Actualizar ejercicio", description = "Actualiza un ejercicio existente por id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ejercicio actualizado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PreAuthorize("@permissionService.canModifyExercise(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(
            @Parameter(description = "ID del ejercicio") @PathVariable Integer id,
            @Parameter(description = "Datos del ejercicio a actualizar") @RequestBody ExerciseRequest request) {
        Exercise updatedExercise = exerciseService.update(id, exerciseMapper.toEntity(request));
        return ResponseEntity.ok(exerciseMapper.toResponse(updatedExercise));
    }

    @Operation(summary = "Eliminar ejercicio", description = "Elimina un ejercicio existente por id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ejercicio eliminado"),
            @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })
    @PreAuthorize("@permissionService.canModifyExercise(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del ejercicio") @PathVariable Integer id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
