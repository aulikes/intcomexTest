package com.intcomex.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO de entrada para crear una categoría.
 * Permite recibir datos básicos y una imagen desde un formulario multipart.
 */
@Setter
@Getter
@Schema(
    description = "Datos para la creación de una nueva categoría",
    example = """
        {
          "categoryName": "SERVIDORES",
          "description": "Categoría para servidores físicos empresariales",
          "image": "file:MultipartFile"
        }
    """
)
public class CategoryCreateRequest {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @NotNull(message = "El nombre de la categoría es obligatorio")
    @Size(max = 200, message = "El nombre no puede tener más de 200 caracteres")
    @Schema(description = "Nombre de la categoría")
    private String categoryName;

    @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
    @Schema(description = "Descripción de la categoría")
    private String description;

    @Schema(
        description = "Imagen de la categoría (formato .jpg, .jpeg, .png, gif.)",
        type = "string",
        format = "binary"
    )
    @NotNull
    private MultipartFile image;
}
