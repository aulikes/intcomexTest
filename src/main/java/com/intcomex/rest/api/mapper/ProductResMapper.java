package com.intcomex.rest.api.mapper;

import com.intcomex.rest.api.dto.ProductGetResponse;
import com.intcomex.rest.api.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductResMapper {

    @Mapping(source = "category.categoryID", target = "categoryID")
    @Mapping(source = "category.categoryName", target = "categoryName")
//    @Mapping(source = "category.categoryImage", target = "categoryImage")
    ProductGetResponse toDTO(Product entity);
    Product toEntity(ProductGetResponse dto);
}
