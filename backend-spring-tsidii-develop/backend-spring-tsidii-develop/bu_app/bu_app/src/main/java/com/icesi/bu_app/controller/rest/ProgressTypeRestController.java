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

import com.icesi.bu_app.controller.rest.dto.ProgressTypeRequest;
import com.icesi.bu_app.controller.rest.dto.ProgressTypeResponse;
import com.icesi.bu_app.mappers.IProgressTypeMapper;
import com.icesi.bu_app.model.ProgressType;
import com.icesi.bu_app.service.IProgressTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/progress-types")
@RequiredArgsConstructor
@Tag(name = "Tipos de progreso", description = "Operaciones sobre tipos de progreso")
public class ProgressTypeRestController {
    private final IProgressTypeMapper progressTypeMapper;
    private final IProgressTypeService progressTypeService;

    @Operation(summary = "Listar todos los tipos de progreso", description = "Retorna todos los tipos de progreso registrados")
    @ApiResponse(responseCode = "200", description = "Listado de tipos de progreso")
    @GetMapping("/all")
    public ResponseEntity<List<ProgressTypeResponse>> getAllProgressTypes() {
        List<ProgressTypeResponse> response = progressTypeMapper
                .progressTypesToProgressTypeResponses(progressTypeService.findAll());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar tipos de progreso paginados", description = "Retorna tipos de progreso con paginación usando page y size")
    @ApiResponse(responseCode = "200", description = "Listado paginado de tipos de progreso")
    @GetMapping
    public ResponseEntity<Page<ProgressTypeResponse>> getAllProgressTypesPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<ProgressTypeResponse> response = progressTypeMapper
                .progressTypesToProgressTypeResponses(progressTypeService.findAll(page, size));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener tipo de progreso por id", description = "Retorna un tipo de progreso específico por su identificador")
    @ApiResponse(responseCode = "200", description = "Tipo de progreso encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProgressTypeResponse> getProgressTypeById(
            @Parameter(description = "ID del tipo de progreso") @PathVariable Integer id) {
        ProgressTypeResponse response = progressTypeMapper
                .progressTypeToProgressTypeResponse(progressTypeService.findById(id));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear tipo de progreso", description = "Crea un nuevo tipo de progreso")
    @ApiResponse(responseCode = "201", description = "Tipo de progreso creado")
    @PostMapping
    public ResponseEntity<ProgressTypeResponse> createProgressType(
            @RequestBody ProgressTypeRequest progressTypeRequest) {
        ProgressType progressType = progressTypeMapper.progressTypeRequestToProgressType(progressTypeRequest);
        ProgressTypeResponse createdResponse = progressTypeMapper
                .progressTypeToProgressTypeResponse(progressTypeService.save(progressType));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @Operation(summary = "Actualizar tipo de progreso", description = "Actualiza un tipo de progreso existente por id")
    @ApiResponse(responseCode = "200", description = "Tipo de progreso actualizado")
    @PutMapping("/{id}")
    public ResponseEntity<ProgressTypeResponse> updateProgressType(
            @RequestBody ProgressTypeRequest progressTypeRequest,
            @Parameter(description = "ID del tipo de progreso") @PathVariable Integer id) {
        ProgressType progressType = progressTypeMapper.progressTypeRequestToProgressType(progressTypeRequest);
        ProgressTypeResponse updatedResponse = progressTypeMapper
                .progressTypeToProgressTypeResponse(progressTypeService.update(id, progressType));

        return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);
    }

    @Operation(summary = "Eliminar tipo de progreso", description = "Elimina un tipo de progreso existente por id")
    @ApiResponse(responseCode = "204", description = "Tipo de progreso eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgressType(
            @Parameter(description = "ID del tipo de progreso") @PathVariable Integer id) {
        progressTypeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
