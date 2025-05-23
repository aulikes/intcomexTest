package com.intcomex.rest.api.controller;

import com.intcomex.rest.api.dto.AuthRequest;
import com.intcomex.rest.api.dto.AuthResponse;
import com.intcomex.rest.api.dto.ErrorResponse;
import com.intcomex.rest.api.service.contract.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Autenticación de usuario",
        description = "Valida credenciales de usuario y retorna un token JWT."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Autenticación exitosa",
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
        )
    )
    @ApiResponse(
        responseCode = "401",
        description = "Credenciales no válidas",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida: parámetros no válidos o faltantes.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Error interno en el servidor",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
