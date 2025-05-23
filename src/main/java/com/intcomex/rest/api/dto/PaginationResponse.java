package com.intcomex.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Responde a la paginación de una entidad")
public class PaginationResponse<T> {

    @Schema(description = "Interface que se usó para la búsqueda (Slice o Page)")
    private String typeInterface;

    @Schema(description = "Lista de elementos contenidos en la página actual")
    private List<T> content;

    @Schema(description = "Número de la página actual")
    private int page;

    @Schema(description = "Cantidad de elementos por página")
    private int size;

    @Schema(description = "Número total de elementos")
    private Long totalElements;

    @Schema(description = "Número total de páginas")
    private Integer totalPages;

    private boolean hasNext;
}
