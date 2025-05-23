package com.intcomex.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Data
@ParameterObject
@Schema(
    description = "Datos requeridos para la paginación",
    example = """
        {
          "page": 0,
          "size": 100
        }
    """
    )
public class PaginationRequest {

    private static final int MAX_SIZE = 100;

    @Min(0)
    @Schema(description = "Número de página (empezando desde 0)")
    private int page = 0;

    @Schema(example = "10", description = "Tamaño de la página (máximo 100)")
    @Min(value = 1, message = "El tamaño mínimo es 1")
    private int size = 10;

    @Schema(description = """
            Indica si se necesita el total de las páginas, **Por Defecto es true**.
            Lo ideal es que en la primera página se requiera el total **(withTotal=true)**, en las posteriores no.
            Por tanto en las páginas posteriores a la primera página, se debe colocar **withTotal=false**.
            Esto mejora el rendimiento debido al tipo de consulta que se hace en la base de datos.
        """)
    private boolean withTotal = true;

    @Schema(
            example = "productName,productID",
            description = "Campos por los que se ordena. Separados por coma."
    )
    private String sortBy = "";

    @Schema(
            example = "asc,desc",
            description = "Direcciones de orden para los campos. Separadas por coma. Debe haber la misma cantidad que en 'sortBy'."
    )
    private String direction = "";

    public Pageable toPageable() {
        int safeSize = Math.min(this.size, MAX_SIZE);
        if (sortBy != null && !sortBy.isBlank()) {
            String[] fields = sortBy.split(",");
            String[] directions = (direction != null) ? direction.split(",") : new String[0];
            List<Sort.Order> orders = new ArrayList<>();

            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].trim();
                String dir = (i < directions.length) ? directions[i].trim().toLowerCase() : "asc";
                Sort.Order order = "desc".equals(dir) ? Sort.Order.desc(field) : Sort.Order.asc(field);
                orders.add(order);
            }
            return PageRequest.of(page, safeSize, Sort.by(orders));
        }
        return PageRequest.of(page, safeSize);
    }
}