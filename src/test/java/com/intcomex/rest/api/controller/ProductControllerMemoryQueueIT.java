package com.intcomex.rest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intcomex.rest.api.config.NoSecurityTestConfig;
import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.entity.Product;
import com.intcomex.rest.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest
@Import(NoSecurityTestConfig.class)
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        value = com.intcomex.rest.api.config.SecurityConfig.class
    )
)
class ProductControllerMemoryQueueIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldPersistProductInDatabase() throws Exception {
        productRepository.deleteAll();

        ProductCreateRequest request = new ProductCreateRequest();
        request.setProductName("Producto desde test");
        request.setQuantityPerUnit("1 unidad");
        request.setUnitPrice(BigDecimal.valueOf(1200));
        request.setUnitsInStock(5);
        request.setUnitsOnOrder(1);
        request.setReorderLevel(2);
        request.setDiscontinued(false);
        request.setCategoryID(1L);
        request.setProveedorID(2L);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        await().atMost(5, SECONDS).untilAsserted(() ->
                assertThat(productRepository.findAll())
                        .anyMatch(p -> p.getProductName().equals("Producto desde test"))
        );
    }
}
