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

import com.icesi.bu_app.controller.rest.dto.RecommendationRequest;
import com.icesi.bu_app.controller.rest.dto.RecommendationResponse;
import com.icesi.bu_app.mappers.IRecommendationMapper;
import com.icesi.bu_app.model.Recommendation;
import com.icesi.bu_app.service.IRecommendationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rest/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recomendaciones", description = "Operaciones sobre recomendaciones")
public class RecommendationRestController {
    private final IRecommendationMapper recommendationMapper;
    private final IRecommendationService recommendationService;

    @Operation(summary = "Listar todas las recomendaciones", description = "Retorna todas las recomendaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado de recomendaciones")
    @GetMapping("/all")
    public ResponseEntity<List<RecommendationResponse>> getAllRecommendations() {
        List<RecommendationResponse> response = recommendationMapper
                .recommendationsToRecommendationResponses(recommendationService.findAll());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Listar recomendaciones paginadas", description = "Retorna recomendaciones con paginación usando page y size.")
    @ApiResponse(responseCode = "200", description = "Listado paginado de recomendaciones")
    @GetMapping
    public ResponseEntity<Page<RecommendationResponse>> getAllRecommendationsPaginated(
            @Parameter(description = "Número de página (0..N)") @RequestParam int page,
            @Parameter(description = "Tamaño de página") @RequestParam int size) {
        Page<RecommendationResponse> response = recommendationMapper
                .recommendationsToRecommendationResponses(recommendationService.findAll(page, size));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Obtener recomendación por id", description = "Retorna una recomendación específica por su identificador.")
    @ApiResponse(responseCode = "200", description = "Recomendación encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationResponse> getRecommendationById(
            @Parameter(description = "ID de la recomendación") @PathVariable Integer id) {
        RecommendationResponse response = recommendationMapper
                .recommendationToRecommendationResponse(recommendationService.findById(id));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Crear recomendación", description = "Crea una recomendación con la información suministrada.")
    @ApiResponse(responseCode = "201", description = "Recomendación creada")
    @PostMapping
    public ResponseEntity<RecommendationResponse> createRecommendation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la recomendación a crear") @RequestBody RecommendationRequest recommendationRequest) {
        Recommendation recommendation = recommendationMapper
                .recommendationRequestToRecommendation(recommendationRequest);
        RecommendationResponse createdResponse = recommendationMapper
                .recommendationToRecommendationResponse(recommendationService.save(recommendation));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @Operation(summary = "Actualizar recomendación", description = "Actualiza una recomendación existente por id.")
    @ApiResponse(responseCode = "200", description = "Recomendación actualizada")
    @PutMapping("/{id}")
    public ResponseEntity<RecommendationResponse> updateRecommendation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la recomendación a actualizar") @RequestBody RecommendationRequest recommendationRequest,
            @Parameter(description = "ID de la recomendación") @PathVariable Integer id) {
        Recommendation recommendation = recommendationMapper
                .recommendationRequestToRecommendation(recommendationRequest);
        RecommendationResponse updatedResponse = recommendationMapper
                .recommendationToRecommendationResponse(recommendationService.update(id, recommendation));

        return ResponseEntity.status(HttpStatus.OK).body(updatedResponse);
    }

    @Operation(summary = "Eliminar recomendación", description = "Elimina una recomendación existente por id.")
    @ApiResponse(responseCode = "204", description = "Recomendación eliminada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(
            @Parameter(description = "ID de la recomendación") @PathVariable Integer id) {
        recommendationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
