package com.intcomex.rest.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productID;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 255, message = "El nombre del producto no debe exceder 255 caracteres")
    private String productName;

    @NotBlank(message = "La presentación del producto es obligatoria")
    @Size(max = 100, message = "La presentación no debe exceder 100 caracteres")
    private String quantityPerUnit;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal unitPrice;

    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer unitsInStock;

    @Min(value = 0, message = "Las unidades en pedido no pueden ser negativas")
    private Integer unitsOnOrder;

    @Min(value = 0, message = "El nivel de reorden no puede ser negativo")
    private Integer reorderLevel;

    @NotNull(message = "Debe indicar si el producto está descontinuado")
    private Boolean discontinued;

    @NotNull(message = "Debe asociar una categoría")
    @ManyToOne
    @JoinColumn(name = "categoryID")
    private Category category;

}
