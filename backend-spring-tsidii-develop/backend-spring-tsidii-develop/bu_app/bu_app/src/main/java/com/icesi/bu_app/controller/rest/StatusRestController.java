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

import com.icesi.bu_app.controller.rest.dto.StatusRequest;
import com.icesi.bu_app.controller.rest.dto.StatusResponse;
import com.icesi.bu_app.mappers.IStatusMapper;
import com.icesi.bu_app.model.Status;
import com.icesi.bu_app.service.IStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/statuses")
@RequiredArgsConstructor
@Tag(name = "Estados", description = "Operaciones sobre estados")
public class StatusRestController {
    private final IStatusMapper statusMapper;
    private final IStatusService statusService;

    @Operation(summary = "Listar todos los estados", description = "Retorna todos los estados registrados")
    @ApiResponse(responseCode = "200", description = "Listado de estados")
    @GetMapping("/all")
    public ResponseEntity<List<StatusResponse>> getAllStatuses() {
        List<StatusResponse> response = statusMapper.statusesToStatusResponses(statusService.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar estados paginados", description = "Retorna estados con paginación usando page y size")
    @ApiResponse(responseCode = "200", description = "Listado paginado de estados")
    @GetMapping
    public ResponseEntity<Page<StatusResponse>> getAllStatusesPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<StatusResponse> response = statusMapper.statusesToStatusResponses(statusService.findAll(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener estado por id", description = "Retorna un estado específico por su identificador")
    @ApiResponse(responseCode = "200", description = "Estado encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<StatusResponse> getStatusById(
            @Parameter(description = "ID del estado") @PathVariable Integer id) {
        StatusResponse response = statusMapper.statusToStatusResponse(statusService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear estado", description = "Crea un nuevo estado")
    @ApiResponse(responseCode = "201", description = "Estado creado")
    @PostMapping
    public ResponseEntity<StatusResponse> createStatus(@RequestBody StatusRequest statusRequest) {
        Status status = statusMapper.statusRequestToStatus(statusRequest);
        StatusResponse created = statusMapper.statusToStatusResponse(statusService.save(status));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar estado", description = "Actualiza un estado existente por id")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @PutMapping("/{id}")
    public ResponseEntity<StatusResponse> updateStatus(@RequestBody StatusRequest statusRequest,
            @Parameter(description = "ID del estado") @PathVariable Integer id) {
        Status status = statusMapper.statusRequestToStatus(statusRequest);
        StatusResponse updated = statusMapper.statusToStatusResponse(statusService.update(id, status));
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "Eliminar estado", description = "Elimina un estado existente por id")
    @ApiResponse(responseCode = "204", description = "Estado eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@Parameter(description = "ID del estado") @PathVariable Integer id) {
        statusService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
