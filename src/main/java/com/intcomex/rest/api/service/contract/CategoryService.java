package com.intcomex.rest.api.service.contract;

import com.intcomex.rest.api.dto.CategoryCreateRequest;
import com.intcomex.rest.api.dto.CategoryCreateResponse;
import com.intcomex.rest.api.entity.Category;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CategoryService {

    CategoryCreateResponse createCategory(CategoryCreateRequest categoryCreateRequest,
                                          HttpServletRequest httpServletRequest);

    CategoryCreateResponse createCategory(String name, String description,
                                          String fileName, byte[] imageBytes, String contentType);

    public List<Long> getAllCategoryIds();

    boolean existOneCategory();

}
