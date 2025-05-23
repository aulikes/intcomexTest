package com.intcomex.rest.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(
    description = "Datos requeridos para crear un producto",
    example = """
        {
          "productName": "Servidor Pro X500",
          "quantityPerUnit": "1 unidad",
          "unitPrice": 14999.99,
          "unitsInStock": 20,
          "unitsOnOrder": 10,
          "reorderLevel": 5,
          "discontinued": false,
          "categoryID": 1
        }
    """
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productName;

    @NotBlank(message = "Debe especificar la cantidad por unidad")
    private String quantityPerUnit;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Debe especificar las unidades en stock")
    @Min(value = 0, message = "Unidades en stock no puede ser negativo")
    private Integer unitsInStock;

    @NotNull(message = "Debe especificar las unidades en orden")
    @Min(value = 0, message = "Unidades en orden no puede ser negativo")
    private Integer unitsOnOrder;

    @Min(value = 0, message = "Nivel de reorden no puede ser negativo")
    private Integer reorderLevel = 0; // Valor predeterminado

    private Boolean discontinued = false; // Valor predeterminado para booleanos

    @NotNull(message = "Debe especificar una categoría")
    private Long categoryID;

//    @NotNull(message = "Debe especificar el proveedor")
    private Long proveedorID;
}
