package com.intcomex.rest.api.controller;

import com.intcomex.rest.api.dto.CategoryCreateRequest;
import com.intcomex.rest.api.dto.CategoryCreateResponse;
import com.intcomex.rest.api.service.contract.CategoryService;
import com.intcomex.rest.api.swagger.DefaultErrApiResponses;
import com.intcomex.rest.api.swagger.DefaultErrAuthResponses;
import com.intcomex.rest.api.swagger.DefaultErrClientResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final HttpServletRequest httpServletRequest;

    @Operation(
        summary = "Crear una nueva categoría",
        description = """
            Este endpoint permite crear una nueva categoría proporcionando los siguientes datos:
            - **Nombre**: el nombre de la categoría.
            - **Descripción**: una descripción de la categoría.
            - **Imagen**: un archivo de imagen enviado como multipart.

            El endpoint devuelve un objeto con los detalles de la categoría creada.
            """
    )
    @ApiResponse(
        responseCode = "201",
        description = "Categoría creada exitosamente.",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CategoryCreateResponse.class)
        )
    )
    @DefaultErrApiResponses
    @DefaultErrAuthResponses
    @DefaultErrClientResponses
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryCreateResponse> createCategory(
            @Valid @ModelAttribute CategoryCreateRequest categoryCreateRequest
    ) {
        CategoryCreateResponse created = categoryService.createCategory(categoryCreateRequest, httpServletRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
