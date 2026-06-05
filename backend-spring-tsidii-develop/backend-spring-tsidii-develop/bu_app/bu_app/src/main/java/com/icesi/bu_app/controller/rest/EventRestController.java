package com.icesi.bu_app.controller.rest;

import java.sql.Timestamp;
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

import com.icesi.bu_app.controller.rest.dto.EventRequest;
import com.icesi.bu_app.controller.rest.dto.EventResponse;
import com.icesi.bu_app.mappers.IEventMapper;
import com.icesi.bu_app.mappers.IAlertMapper;
import com.icesi.bu_app.model.Event;
import com.icesi.bu_app.model.Alert;
import com.icesi.bu_app.model.User;
import com.icesi.bu_app.service.IEventService;
import com.icesi.bu_app.service.IAlertService;
import com.icesi.bu_app.repository.IUserRepository;
import com.icesi.bu_app.ws.AlertsWebSocketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/events")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Operaciones sobre eventos")
public class EventRestController {
	private final IEventMapper eventMapper;
	private final IEventService eventService;
	private final IAlertMapper alertMapper;
	private final IAlertService alertService;
	private final IUserRepository userRepository;
	private final AlertsWebSocketService alertsWebSocketService;

	@Operation(summary = "Listar todos los eventos", description = "Retorna todos los eventos registrados.")
	@ApiResponse(responseCode = "200", description = "Listado de eventos", useReturnTypeSchema = true)
	@GetMapping("/all")
	public ResponseEntity<List<EventResponse>> getAllEvents() {
		List<EventResponse> response = eventMapper.eventsToEventResponses(eventService.findAll());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Listar eventos paginados", description = "Retorna eventos con paginación usando page y size.")
	@ApiResponse(responseCode = "200", description = "Listado paginado de eventos", useReturnTypeSchema = true)
	@GetMapping
	public ResponseEntity<Page<EventResponse>> getAllEventsPaginated(
			@Parameter(description = "Número de página (0..N)") @RequestParam int page,
			@Parameter(description = "Tamaño de página") @RequestParam int size) {
		Page<EventResponse> response = eventMapper.eventsToEventResponses(eventService.findAll(page, size));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Obtener evento por id", description = "Retorna un evento específico por su identificador.")
	@ApiResponse(responseCode = "200", description = "Evento encontrado", useReturnTypeSchema = true)
	@GetMapping("/{id}")
	public ResponseEntity<EventResponse> getEventById(
			@Parameter(description = "ID del evento") @PathVariable Integer id) {
		EventResponse response = eventMapper.eventToEventResponse(eventService.findById(id));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Crear evento", description = "Crea un nuevo evento con la información suministrada.")
	@ApiResponse(responseCode = "201", description = "Evento creado", useReturnTypeSchema = true)
	@PostMapping
	public ResponseEntity<EventResponse> createEvent(
			@Parameter(description = "Datos del evento a crear") @RequestBody EventRequest eventRequest) {
		Event event = eventMapper.eventRequestToEvent(eventRequest);
		Event savedEvent = eventService.save(event);
		EventResponse created = eventMapper.eventToEventResponse(savedEvent);

		// Notify all trainees about the new event and persist alerts
		List<User> trainees = userRepository.findByRoleTypeAndIsDeletedFalse("TRAINEE");
		for (User t : trainees) {
			Alert a = new Alert();
			a.setMessage("Nuevo evento: " + savedEvent.getName());
			a.setSendDate(new Timestamp(System.currentTimeMillis()));
			a.setTrainer(savedEvent.getManager());
			a.setTrainee(t);
			Alert saved = alertService.save(a);
			try {
				alertsWebSocketService.broadcast(alertMapper.alertToAlertResponse(saved));
			} catch (Exception e) {
				// ignore websocket errors
			}
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@Operation(summary = "Actualizar evento", description = "Actualiza un evento existente por id.")
	@ApiResponse(responseCode = "200", description = "Evento actualizado", useReturnTypeSchema = true)
	@PutMapping("/{id}")
	public ResponseEntity<EventResponse> updateEvent(
			@Parameter(description = "Datos del evento a actualizar") @RequestBody EventRequest eventRequest,
			@Parameter(description = "ID del evento") @PathVariable Integer id) {
		Event event = eventMapper.eventRequestToEvent(eventRequest);
		EventResponse updated = eventMapper.eventToEventResponse(eventService.update(id, event));
		return ResponseEntity.status(HttpStatus.OK).body(updated);
	}

	@Operation(summary = "Eliminar evento", description = "Elimina un evento existente por id.")
	@ApiResponse(responseCode = "204", description = "Evento eliminado", useReturnTypeSchema = true)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvent(@Parameter(description = "ID del evento") @PathVariable Integer id) {
		eventService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
