package com.intcomex.rest.api.controller;

import com.intcomex.rest.api.dto.*;
import com.intcomex.rest.api.service.contract.ProductService;
import com.intcomex.rest.api.service.queue.ProductCommandEnqueuer;
import com.intcomex.rest.api.swagger.DefaultErrApiResponses;
import com.intcomex.rest.api.swagger.DefaultErrAuthResponses;
import com.intcomex.rest.api.swagger.DefaultErrClientResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final ProductCommandEnqueuer productCommandEnqueuer;

    @Operation(
            summary = "Encolar un nuevo producto para procesamiento asíncrono",
            description = "Recibe un producto y lo encola para ser procesado en segundo plano. La inserción en base de datos no es inmediata."
    )
    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        productCommandEnqueuer.enqueue(request);
        log.debug("Solicitud de creación de producto encolada: {}", request.getProductName());
        return ResponseEntity.accepted().build(); // 202 porque el procesamiento es asincrónico
    }

    @Operation(
        summary = "Listar todos los productos",
        description = """
            Devuelve una lista paginada de productos.
            Se puede ordenar por uno o más campos usando los parámetros `sortBy` y `direction`.
    
            - Ejemplo 1: `/api/products/list`
            - Ejemplo 2: `/api/products/list?withTotal=false&page=2&size=150`
            - Ejemplo 3: `/api/products/list?page=0&size=15&sortBy=productName&direction=asc`
            - Ejemplo 4: `/api/products/list?withTotal=true&page=1&size=200&sortBy=productName,productID&direction=asc,desc`
            - Ejemplo 5: `/api/products/list?page=0&size=15&sortBy=productName,productID&direction=asc,desc`
    
            Si no se proporciona `direction` para algún campo, se usará `asc` por defecto.
            El tamaño máximo de página (`size`) permitido es 100; si se supera, se ajusta automáticamente.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))
    )
    @DefaultErrApiResponses
    @DefaultErrAuthResponses
    @DefaultErrClientResponses
    @GetMapping("/list")
    public ResponseEntity<PaginationResponse<ProductGetResponse>> listAll(
            @Valid @ModelAttribute PaginationRequest pagination
    ) {
        PaginationResponse<ProductGetResponse> result = service.getAllProductsPaginated(pagination);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Retorna los detalles de un producto específico, incluyendo la imagen de su categoría"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductGetResponse.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Conflicto: ya existe un producto con ese nombre",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = com.intcomex.rest.api.dto.ErrorResponse.class)
            )
    )
    @DefaultErrApiResponses
    @DefaultErrAuthResponses
    @DefaultErrClientResponses
    @GetMapping("/{id}")
    public ProductGetResponse getById(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable Long id
    ) {
        return service.getProductById(id);
    }
}

