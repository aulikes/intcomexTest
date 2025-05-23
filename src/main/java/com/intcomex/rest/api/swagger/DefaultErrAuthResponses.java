package com.intcomex.rest.api.swagger;

import com.intcomex.rest.api.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "No autorizado: token JWT faltante, inválido o expirado.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Acceso denegado: el usuario no tiene permisos suficientes.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DefaultErrAuthResponses {}
