package com.intcomex.rest.api.mapper;

import org.mapstruct.Mapper;
import com.intcomex.rest.api.dto.CategoryCreateResponse;
import com.intcomex.rest.api.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryCreateResponse toDTO(Category entity);
    Category toEntity(CategoryCreateResponse dto);
}
