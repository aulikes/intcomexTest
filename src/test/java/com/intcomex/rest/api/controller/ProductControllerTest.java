package com.intcomex.rest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intcomex.rest.api.config.NoSecurityTestConfig;
import com.intcomex.rest.api.dto.PaginationResponse;
import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.dto.ProductGetResponse;
import com.intcomex.rest.api.service.contract.ProductService;
import com.intcomex.rest.api.service.queue.ProductCommandEnqueuer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({NoSecurityTestConfig.class, ProductControllerTest.TestConfig.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductCommandEnqueuer productCommandEnqueuer;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ProductCommandEnqueuer productCommandEnqueuer() {
            return mock(ProductCommandEnqueuer.class);
        }

        @Bean
        @Primary
        public ProductService productService() {
            return mock(ProductService.class);
        }
    }

    @Test
    @DisplayName("Debe retornar 202 Accepted si el producto es encolado correctamente")
    void shouldEnqueueProductSuccessfully() throws Exception {
        ProductCreateRequest request = createValidRequest();
        doNothing().when(productCommandEnqueuer).enqueue(any());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Debe retornar 400 si el producto es no válido")
    void shouldReturnBadRequest() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 200 con productos paginados")
    void shouldReturnPaginatedProductList() throws Exception {
        PaginationResponse<ProductGetResponse> paginationResponse = getProductGetResponsePaginationResponse();

        when(productService.getAllProductsPaginated(any())).thenReturn(paginationResponse);

        mockMvc.perform(get("/api/products/list")
                        .param("page", "0")
                        .param("size", "10")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].productName").value("Router"))
                .andExpect(jsonPath("$.content[1].productName").value("Switch"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @DisplayName("Debe retornar 200 con página vacía si no hay productos")
    void shouldReturnEmptyPage() throws Exception {
        PaginationResponse<ProductGetResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setPage(0);
        paginationResponse.setSize(10);
        paginationResponse.setTotalPages(1);
        paginationResponse.setTotalElements(0L);
        paginationResponse.setContent(List.of());
        when(productService.getAllProductsPaginated(any())).thenReturn(paginationResponse);

        mockMvc.perform(get("/api/products/list?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("Debe retornar 200 al obtener un producto por ID")
    void shouldReturnProductById() throws Exception {
        ProductGetResponse mockProduct = new ProductGetResponse();
        mockProduct.setProductID(1L);
        mockProduct.setProductName("Servidor Pro X500");

        when(productService.getProductById(1L)).thenReturn(mockProduct);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productID").value(1))
                .andExpect(jsonPath("$.productName").value("Servidor Pro X500"));
    }

    private static PaginationResponse<ProductGetResponse> getProductGetResponsePaginationResponse() {
        ProductGetResponse prod1 = new ProductGetResponse();
        prod1.setProductID(1L);
        prod1.setProductName("Router");
        prod1.setUnitPrice(new BigDecimal("89.99"));

        ProductGetResponse prod2 = new ProductGetResponse();
        prod2.setProductID(2L);
        prod2.setProductName("Switch");
        prod2.setUnitPrice(new BigDecimal("129.99"));

        List<ProductGetResponse> products = List.of(prod1, prod2);

        PaginationResponse<ProductGetResponse> paginationResponse = new PaginationResponse<>();
        paginationResponse.setPage(0);
        paginationResponse.setSize(10);
        paginationResponse.setTotalPages(1);
        paginationResponse.setTotalElements((long) products.size());
        paginationResponse.setContent(products);
        return paginationResponse;
    }

    private ProductCreateRequest createValidRequest() {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setProductName("Servidor Pro X500");
        request.setQuantityPerUnit("1 unidad");
        request.setUnitPrice(BigDecimal.valueOf(14999.99));
        request.setUnitsInStock(20);
        request.setUnitsOnOrder(10);
        request.setReorderLevel(5);
        request.setDiscontinued(false);
        request.setCategoryID(1L);
        request.setProveedorID(2L);
        return request;
    }



}
