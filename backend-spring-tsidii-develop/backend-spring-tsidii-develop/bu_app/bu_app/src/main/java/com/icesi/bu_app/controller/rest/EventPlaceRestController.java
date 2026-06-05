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

import com.icesi.bu_app.controller.rest.dto.EventPlaceRequest;
import com.icesi.bu_app.controller.rest.dto.EventPlaceResponse;
import com.icesi.bu_app.mappers.IEventPlaceMapper;
import com.icesi.bu_app.mappers.IAlertMapper;
import com.icesi.bu_app.model.EventPlace;
import com.icesi.bu_app.model.Alert;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.service.IEventPlaceService;
import com.icesi.bu_app.service.IAlertService;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.ws.AlertsWebSocketService;
import java.sql.Timestamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/events-places")
@RequiredArgsConstructor
@Tag(name = "Eventos-Lugares", description = "Operaciones sobre la relación evento-lugar")
public class EventPlaceRestController {
    private final IEventPlaceMapper eventPlaceMapper;
    private final IEventPlaceService eventPlaceService;
    private final IAlertMapper alertMapper;
    private final IAlertService alertService;
    private final IUserRepository userRepository;
    private final AlertsWebSocketService alertsWebSocketService;

    @Operation(summary = "Listar todas las relaciones evento-lugar", description = "Retorna todas las relaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado de relaciones", useReturnTypeSchema = true)
    @GetMapping("/all")
    public ResponseEntity<List<EventPlaceResponse>> getAll() {
        List<EventPlaceResponse> response = eventPlaceMapper
                .eventPlacesToEventPlaceResponses(eventPlaceService.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar relaciones evento-lugar paginadas", description = "Retorna relaciones con paginación usando page y size.")
    @ApiResponse(responseCode = "200", description = "Listado paginado de relaciones", useReturnTypeSchema = true)
    @GetMapping
    public ResponseEntity<Page<EventPlaceResponse>> getAllPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<EventPlaceResponse> response = eventPlaceMapper
                .eventPlacesToEventPlaceResponses(eventPlaceService.findAll(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener relación evento-lugar por id", description = "Retorna una relación específica por su identificador.")
    @ApiResponse(responseCode = "200", description = "Relación encontrada", useReturnTypeSchema = true)
    @GetMapping("/{id}")
    public ResponseEntity<EventPlaceResponse> getById(
            @Parameter(description = "ID de la relación") @PathVariable Integer id) {
        EventPlaceResponse response = eventPlaceMapper.eventPlaceToEventPlaceResponse(eventPlaceService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear relación evento-lugar", description = "Crea una nueva relación evento-lugar con la información suministrada.")
    @ApiResponse(responseCode = "201", description = "Relación creada", useReturnTypeSchema = true)
    @PostMapping
    public ResponseEntity<EventPlaceResponse> create(
            @Parameter(description = "Datos de la relación a crear") @RequestBody EventPlaceRequest request) {
        EventPlace ep = eventPlaceMapper.eventPlaceRequestToEventPlace(request);
        EventPlace saved = eventPlaceService.save(ep);
        EventPlaceResponse created = eventPlaceMapper.eventPlaceToEventPlaceResponse(saved);

        // Notify all trainees about the new space and persist alerts
        List<User> trainees = userRepository.findByRoleTypeAndIsDeletedFalse("TRAINEE");
        for (User t : trainees) {
            Alert a = new Alert();
            a.setMessage("Nuevo espacio: " + saved.getEvent().getName());
            a.setSendDate(new Timestamp(System.currentTimeMillis()));
            a.setTrainer(saved.getEvent().getManager());
            a.setTrainee(t);
            Alert savedAlert = alertService.save(a);
            try {
                alertsWebSocketService.broadcast(alertMapper.alertToAlertResponse(savedAlert));
            } catch (Exception e) {
                // ignore websocket errors
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar relación evento-lugar", description = "Actualiza una relación existente por id.")
    @ApiResponse(responseCode = "200", description = "Relación actualizada", useReturnTypeSchema = true)
    @PutMapping("/{id}")
    public ResponseEntity<EventPlaceResponse> update(
            @Parameter(description = "Datos de la relación a actualizar") @RequestBody EventPlaceRequest request,
            @Parameter(description = "ID de la relación") @PathVariable Integer id) {
        EventPlace ep = eventPlaceMapper.eventPlaceRequestToEventPlace(request);
        EventPlaceResponse updated = eventPlaceMapper.eventPlaceToEventPlaceResponse(eventPlaceService.update(id, ep));
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "Eliminar relación evento-lugar", description = "Elimina una relación existente por id.")
    @ApiResponse(responseCode = "204", description = "Relación eliminada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID de la relación") @PathVariable Integer id) {
        eventPlaceService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
