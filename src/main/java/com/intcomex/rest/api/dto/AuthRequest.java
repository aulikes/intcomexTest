package com.intcomex.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para loguearse y obtener un Token.
 */
@Setter
@Getter
@Schema(
    description = "Generación de tokens JWT para acceso seguro a la API",
    example = """
        {
          "username": "userIntcomex",
          "password": "userIntcomex123456"
        }
    """
)
public class AuthRequest {

    @NotBlank(message = "El usuario es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}
