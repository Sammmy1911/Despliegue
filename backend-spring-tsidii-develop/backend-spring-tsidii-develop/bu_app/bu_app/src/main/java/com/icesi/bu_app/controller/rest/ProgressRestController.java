package com.icesi.bu_app.controller.rest;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

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

import com.icesi.bu_app.controller.rest.dto.ProgressAggregationResponse;
import com.icesi.bu_app.controller.rest.dto.ProgressRequest;
import com.icesi.bu_app.controller.rest.dto.ProgressResponse;
import com.icesi.bu_app.mappers.IProgressMapper;
import com.icesi.bu_app.model.Progress;
import com.icesi.bu_app.service.IProgressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/rest/progresses")
@RequiredArgsConstructor
@Tag(name = "Progresos", description = "Operaciones sobre progresos")
public class ProgressRestController {
    private final IProgressMapper progressMapper;
    private final IProgressService progressService;

    @Operation(summary = "Listar todos los progresos", description = "Retorna todos los progresos registrados.")
    @ApiResponse(responseCode = "200", description = "Listado de progresos")
    @PreAuthorize("@permissionService.isAdmin(authentication)")
    @GetMapping("/all")
    public ResponseEntity<List<ProgressResponse>> getAllProgresses() {
        List<ProgressResponse> response = progressMapper.progressesToProgressResponses(progressService.findAll());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar progresos paginados", description = "Retorna progresos con paginación usando page y size.")
    @ApiResponse(responseCode = "200", description = "Listado paginado de progresos")
    @PreAuthorize("@permissionService.isAdmin(authentication)")
    @GetMapping
    public ResponseEntity<Page<ProgressResponse>> getAllProgressesPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<ProgressResponse> response = progressMapper
                .progressesToProgressResponses(progressService.findAll(page, size));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener progreso por id", description = "Retorna un progreso específico por su identificador.")
    @ApiResponse(responseCode = "200", description = "Progreso encontrado")
    @PreAuthorize("@permissionService.canViewProgress(authentication, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<ProgressResponse> getProgressById(
            @Parameter(description = "ID del progreso") @PathVariable Integer id) {
        ProgressResponse response = progressMapper.progressToProgressResponse(progressService.findById(id));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear progreso", description = "Crea un progreso con la información suministrada.")
    @ApiResponse(responseCode = "201", description = "Progreso creado")
    @PreAuthorize("@permissionService.canManageProgress(authentication, #progressRequest.traineeId)")
    @PostMapping
    public ResponseEntity<ProgressResponse> createProgress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del progreso a crear") @RequestBody ProgressRequest progressRequest) {
        Progress progress = progressMapper.progressRequestToProgress(progressRequest);
        ProgressResponse createdResponse = progressMapper.progressToProgressResponse(progressService.save(progress));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @Operation(summary = "Actualizar progreso", description = "Actualiza un progreso existente por id.")
    @ApiResponse(responseCode = "200", description = "Progreso actualizado")
    @PreAuthorize("@permissionService.canModifyProgress(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ProgressResponse> updateProgress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del progreso a actualizar") @RequestBody ProgressRequest progressRequest,
            @Parameter(description = "ID del progreso") @PathVariable Integer id) {
        Progress progress = progressMapper.progressRequestToProgress(progressRequest);
        ProgressResponse updatedResponse = progressMapper
                .progressToProgressResponse(progressService.update(id, progress));

        return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);
    }

    @Operation(summary = "Eliminar progreso", description = "Elimina un progreso existente por id.")
    @ApiResponse(responseCode = "204", description = "Progreso eliminado")
    @PreAuthorize("@permissionService.canModifyProgress(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@Parameter(description = "ID del progreso") @PathVariable Integer id) {
        progressService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Buscar progresos por rango de fecha", description = "Busca progresos entre dos timestamps ISO (yyyy-MM-ddTHH:mm:ss). Opcional traineeId para filtrar por usuario.")
    @PreAuthorize("@permissionService.canViewTrainee(authentication, #traineeId) or @permissionService.isAdmin(authentication)")
    @GetMapping("/search")
    public ResponseEntity<List<ProgressResponse>> searchByRange(
            @Parameter(description = "Fecha/hora inicio (ISO_LOCAL_DATE_TIME) e.g. 2024-10-01T00:00:00") @RequestParam String from,
            @Parameter(description = "Fecha/hora fin (ISO_LOCAL_DATE_TIME)") @RequestParam String to,
            @Parameter(description = "ID del trainee (opcional)") @RequestParam(required = false) Integer traineeId) {

        LocalDateTime startLdt = LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endLdt = LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Timestamp start = Timestamp.valueOf(startLdt);
        Timestamp end = Timestamp.valueOf(endLdt);

        if (traineeId != null) {
            List<ProgressResponse> response = progressMapper
                    .progressesToProgressResponses(
                            progressService.findByTraineeAndPerformedAtBetween(traineeId, start, end));
            return ResponseEntity.ok(response);
        } else {
            List<ProgressResponse> response = progressMapper
                    .progressesToProgressResponses(progressService.findByPerformedAtBetween(start, end));
            return ResponseEntity.ok(response);
        }
    }

    @Operation(summary = "Agregar/obtener agregados de progresos por periodo", description = "Retorna agregaciones por 'daily' o 'weekly' entre dos timestamps. traineeId opcional.")
    @PreAuthorize("@permissionService.canViewTrainee(authentication, #traineeId) or @permissionService.isAdmin(authentication)")
    @GetMapping("/aggregated")
    public ResponseEntity<List<com.icesi.bu_app.controller.rest.dto.ProgressAggregationResponse>> aggregate(
            @Parameter(description = "Periodo: daily|weekly") @RequestParam String period,
            @Parameter(description = "Fecha/hora inicio (ISO_LOCAL_DATE_TIME)") @RequestParam String from,
            @Parameter(description = "Fecha/hora fin (ISO_LOCAL_DATE_TIME)") @RequestParam String to,
            @Parameter(description = "ID del trainee (opcional)") @RequestParam(required = false) Integer traineeId) {

        LocalDateTime startLdt = LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endLdt = LocalDateTime.parse(to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Timestamp start = Timestamp.valueOf(startLdt);
        Timestamp end = Timestamp.valueOf(endLdt);

        List<ProgressAggregationResponse> agg = progressService.aggregate(period, start, end, traineeId);

        return ResponseEntity.ok(agg);
    }
}
