package com.intcomex.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@Schema(
    description = "Estructura estándar de respuesta para errores",
    example = """
        {
          "timestamp": "2025-05-17T18:45:00Z",
          "status": "XXX",
          "error": "Descripción de error HTTP",
          "message": "Mensaje de acuerdo a la excepción lanzada",
          "path": "Endpoint de la excepción"
        }
        """
)
public class ErrorResponse {

    @Schema(example = "2025-05-17T19:34:31.2498846-05:00", description = "Momento en que ocurrió el error")
    private ZonedDateTime timestamp;

    @Schema(example = "400", description = "Código HTTP del error")
    private String status;

    @Schema(example = "Bad Request", description = "Tipo de error HTTP")
    private String error;

    @Schema(example = "El campo 'productName' es obligatorio", description = "Descripción detallada del error")
    private String message;

    @Schema(example = "/products", description = "Ruta donde ocurrió el error")
    private String path;
}
