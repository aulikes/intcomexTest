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
                responseCode = "400",
                description = "Solicitud no válida: parámetros incorrectos, tipos erróneos o datos inválidos.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Recurso no encontrado: la entidad solicitada no existe.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "405",
                description = "Método HTTP no permitido para esta ruta.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "415",
                description = "Tipo de contenido no soportado (ej. se esperaba application/json).",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "429",
                description = "Demasiadas peticiones: el cliente ha superado el límite permitido.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DefaultErrClientResponses {}
