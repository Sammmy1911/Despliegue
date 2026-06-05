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

import com.icesi.bu_app.controller.rest.dto.AlertRequest;
import com.icesi.bu_app.controller.rest.dto.AlertResponse;
import com.icesi.bu_app.mappers.IAlertMapper;
import com.icesi.bu_app.model.Alert;
import com.icesi.bu_app.service.IAlertService;
import com.icesi.bu_app.ws.AlertsWebSocketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.icesi.bu_app.service.IPermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/alerts")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Operaciones sobre alertas")
public class AlertRestController {
        private final IAlertMapper alertMapper;
        private final IAlertService alertService;
        private final AlertsWebSocketService alertsWebSocketService;
        private final IPermissionService permissionService;

    @Operation(summary = "Listar todas las alertas", description = "Retorna todas las alertas registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de alertas", useReturnTypeSchema = true)
    })
    @GetMapping("/all")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        List<AlertResponse> response = alertMapper.alertsToAlertResponses(alertService.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar alertas paginadas", description = "Retorna alertas con paginación usando page y size.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado paginado de alertas", useReturnTypeSchema = true)
    })
    @GetMapping
    public ResponseEntity<Page<AlertResponse>> getAllAlertsPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<AlertResponse> response = alertMapper.alertsToAlertResponses(alertService.findAll(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener alerta por id", description = "Retorna una alerta específica por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerta encontrada", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(
            @Parameter(description = "ID de la alerta") @PathVariable Integer id) {
        AlertResponse response = alertMapper.alertToAlertResponse(alertService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear alerta", description = "Crea una nueva alerta con la información suministrada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Alerta creada", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    @PreAuthorize("@permissionService.canViewTrainee(authentication, #alertRequest.traineeId) or @permissionService.isAdmin(authentication)")
    public ResponseEntity<AlertResponse> createAlert(
            @Parameter(description = "Datos de la alerta a crear") @RequestBody AlertRequest alertRequest) {
        Alert alert = alertMapper.alertRequestToAlert(alertRequest);
                AlertResponse created = alertMapper.alertToAlertResponse(alertService.save(alert));
                try {
                        alertsWebSocketService.broadcast(created);
                } catch (Exception e) {
                        // do not fail request if broadcast fails
                }

                return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar alerta", description = "Actualiza una alerta existente por id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerta actualizada", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlertResponse> updateAlert(
            @Parameter(description = "Datos de la alerta a actualizar") @RequestBody AlertRequest alertRequest,
            @Parameter(description = "ID de la alerta") @PathVariable Integer id,
            Authentication authentication) {
        Alert existing = alertService.findById(id);
        Integer traineeCode = existing.getTrainee().getCode();
        if (!permissionService.canViewTrainee(authentication, traineeCode) && !permissionService.isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Alert alert = alertMapper.alertRequestToAlert(alertRequest);
        AlertResponse updated = alertMapper.alertToAlertResponse(alertService.update(id, alert));
                try {
                        alertsWebSocketService.broadcast(updated);
                } catch (Exception e) {
                        // ignore
                }
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "Eliminar alerta", description = "Elimina una alerta existente por id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alerta eliminada"),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@Parameter(description = "ID de la alerta") @PathVariable Integer id,
            Authentication authentication) {
        Alert existing = alertService.findById(id);
        Integer traineeCode = existing.getTrainee().getCode();
        if (!permissionService.canViewTrainee(authentication, traineeCode) && !permissionService.isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        alertService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
