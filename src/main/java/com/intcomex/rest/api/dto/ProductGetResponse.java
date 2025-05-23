package com.intcomex.rest.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ProductGetResponse {
    private Long productID;
    private String productName;
    private String quantityPerUnit;
    private BigDecimal unitPrice;
    private Integer unitsInStock;
    private Integer unitsOnOrder;
    private Integer reorderLevel;
    private Boolean discontinued;
    private Long categoryID;
    private String categoryName;
    private String categoryImage;
}
