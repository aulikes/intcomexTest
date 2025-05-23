package com.intcomex.rest.api.mapper;

import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductReqMapper {
    ProductCreateRequest toDTO(Product entity);
    Product toEntity(ProductCreateRequest dto);
}
