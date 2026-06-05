package com.icesi.bu_app.controller.rest;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icesi.bu_app.controller.rest.dto.StressLevelRequest;
import com.icesi.bu_app.controller.rest.dto.StressLevelResponse;
import com.icesi.bu_app.mappers.IStressLevelMapper;
import com.icesi.bu_app.model.StressLevel;
import com.icesi.bu_app.service.IStressLevelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/stress-levels")
@RequiredArgsConstructor
@Tag(name = "Niveles de estrés", description = "Operaciones sobre niveles de estrés")
public class StressLevelRestController {

    private final IStressLevelService stressLevelService;
    private final IStressLevelMapper stressLevelMapper;

    @Operation(summary = "Listar todos los niveles de estrés", description = "Retorna todos los niveles de estrés registrados.")
    @ApiResponse(responseCode = "200", description = "Listado de niveles de estrés")
    @GetMapping("/all")
    public ResponseEntity<List<StressLevelResponse>> getAllStressLevels() {
        List<StressLevelResponse> response = stressLevelMapper
                .stressLevelsToStressLevelResponses(stressLevelService.findAll());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar niveles de estrés paginados", description = "Retorna niveles con paginación usando page y size.")
    @ApiResponse(responseCode = "200", description = "Listado paginado de niveles de estrés")
    @GetMapping
    public ResponseEntity<Page<StressLevelResponse>> getAllStressLevelsPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<StressLevelResponse> response = stressLevelMapper
                .stressLevelsToStressLevelResponses(stressLevelService.findAll(page, size));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener nivel de estrés por id", description = "Retorna un nivel específico por su identificador.")
    @ApiResponse(responseCode = "200", description = "Nivel de estrés encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<StressLevelResponse> getStressLevelById(
            @Parameter(description = "ID del nivel de estrés") @PathVariable Integer id) {
        StressLevelResponse response = stressLevelMapper
                .stressLevelToStressLevelResponse(stressLevelService.findById(id));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear nivel de estrés", description = "Crea un nivel de estrés con la información suministrada.")
    @ApiResponse(responseCode = "201", description = "Nivel de estrés creado")
    @PostMapping
    public ResponseEntity<StressLevelResponse> createStressLevel(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nivel de estrés a crear") @RequestBody StressLevelRequest stressLevelRequest) {
        StressLevel stressLevel = stressLevelMapper.stressLevelRequestToStressLevel(stressLevelRequest);
        StressLevelResponse createdResponse = stressLevelMapper
                .stressLevelToStressLevelResponse(stressLevelService.save(stressLevel));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @Operation(summary = "Actualizar nivel de estrés", description = "Actualiza un nivel existente por id.")
    @ApiResponse(responseCode = "200", description = "Nivel de estrés actualizado")
    @PutMapping("/{id}")
    public ResponseEntity<StressLevelResponse> updateStressLevel(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nivel de estrés a actualizar") @RequestBody StressLevelRequest stressLevelRequest,
            @Parameter(description = "ID del nivel de estrés") @PathVariable Integer id) {
        StressLevel stressLevel = stressLevelMapper.stressLevelRequestToStressLevel(stressLevelRequest);
        StressLevelResponse updatedResponse = stressLevelMapper
                .stressLevelToStressLevelResponse(stressLevelService.update(id, stressLevel));

        return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);
    }

    @Operation(summary = "Eliminar nivel de estrés", description = "Elimina un nivel existente por id.")
    @ApiResponse(responseCode = "204", description = "Nivel de estrés eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStressLevel(
            @Parameter(description = "ID del nivel de estrés") @PathVariable Integer id) {
        stressLevelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
