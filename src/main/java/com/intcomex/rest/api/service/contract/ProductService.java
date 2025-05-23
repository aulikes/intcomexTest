package com.intcomex.rest.api.service.contract;

import com.intcomex.rest.api.dto.*;

import java.util.List;

public interface ProductService {

    PaginationResponse<ProductGetResponse> getAllProductsPaginated(PaginationRequest pagination);

    ProductGetResponse getProductById(Long id);

    void createProduct(List<ProductCreateRequest> productList);

    long countAllProducts();
}
