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

import com.icesi.bu_app.controller.rest.dto.PlaceRequest;
import com.icesi.bu_app.controller.rest.dto.PlaceResponse;
import com.icesi.bu_app.mappers.IPlaceMapper;
import com.icesi.bu_app.model.Place;
import com.icesi.bu_app.service.IPlaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/places")
@RequiredArgsConstructor
@Tag(name = "Lugares", description = "Operaciones sobre lugares")
public class PlaceRestController {
    private final IPlaceMapper placeMapper;
    private final IPlaceService placeService;

    @Operation(summary = "Listar todos los lugares", description = "Retorna todos los lugares registrados.")
    @ApiResponse(responseCode = "200", description = "Listado de lugares")
    @GetMapping("/all")
    public ResponseEntity<List<PlaceResponse>> getAllPlaces() {
        List<PlaceResponse> response = placeMapper.placesToPlaceResponses(placeService.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar lugares paginados", description = "Retorna lugares con paginación usando page y size.")
    @ApiResponse(responseCode = "200", description = "Listado paginado de lugares")
    @GetMapping
    public ResponseEntity<Page<PlaceResponse>> getAllPlacesPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<PlaceResponse> response = placeMapper.placesToPlaceResponses(placeService.findAll(page, size));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener lugar por id", description = "Retorna un lugar específico por su identificador.")
    @ApiResponse(responseCode = "200", description = "Lugar encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getPlaceById(
            @Parameter(description = "ID del lugar") @PathVariable Integer id) {
        PlaceResponse response = placeMapper.placeToPlaceResponse(placeService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear lugar", description = "Crea un lugar con la información suministrada.")
    @ApiResponse(responseCode = "201", description = "Lugar creado")
    @PostMapping
    public ResponseEntity<PlaceResponse> createPlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del lugar a crear") @RequestBody PlaceRequest placeRequest) {
        Place place = placeMapper.placeRequestToPlace(placeRequest);
        PlaceResponse created = placeMapper.placeToPlaceResponse(placeService.save(place));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar lugar", description = "Actualiza un lugar existente por id.")
    @ApiResponse(responseCode = "200", description = "Lugar actualizado")
    @PutMapping("/{id}")
    public ResponseEntity<PlaceResponse> updatePlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del lugar a actualizar") @RequestBody PlaceRequest placeRequest,
            @Parameter(description = "ID del lugar") @PathVariable Integer id) {
        Place place = placeMapper.placeRequestToPlace(placeRequest);
        PlaceResponse updated = placeMapper.placeToPlaceResponse(placeService.update(id, place));
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "Eliminar lugar", description = "Elimina un lugar existente por id.")
    @ApiResponse(responseCode = "204", description = "Lugar eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@Parameter(description = "ID del lugar") @PathVariable Integer id) {
        placeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
