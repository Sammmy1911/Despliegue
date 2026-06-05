package com.icesi.bu_app.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icesi.bu_app.controller.rest.dto.AuthenticatedUserResponse;
import com.icesi.bu_app.controller.rest.dto.LoginRequest;
import com.icesi.bu_app.controller.rest.dto.TokenResponse;
import com.icesi.bu_app.security.CustomUserDetails;
import com.icesi.bu_app.service.IAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/rest/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación")
public class AuthRestController {

    @Autowired
    private IAuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Recibe credenciales y retorna un token JWT si la autenticación es válida.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse token = authService.login(request);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @Operation(summary = "Usuario autenticado", description = "Retorna la identidad del usuario autenticado, útil para inicializar el frontend y aplicar rutas por rol.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado retornado", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails,
            Authentication authentication) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new AuthenticatedUserResponse(
                userDetails.getUser().getCode(),
                userDetails.getUser().getName(),
                userDetails.getUser().getEmail(),
                userDetails.getUser().getRole() == null ? null : userDetails.getUser().getRole().getType(),
                authorities,
                userDetails.getUser().getTrainer() == null ? null : userDetails.getUser().getTrainer().getCode()));
    }

}
