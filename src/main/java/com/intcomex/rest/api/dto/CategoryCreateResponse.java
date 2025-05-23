package com.intcomex.rest.api.dto;

import lombok.Data;

@Data
public class CategoryCreateResponse {
    private Long categoryID;
    private String categoryName;
    private String description;
    private String picture;
}
