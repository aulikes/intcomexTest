package com.intcomex.rest.api.async;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.service.contract.CategoryService;
import com.intcomex.rest.api.service.contract.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitialProductLoaderTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private BlockingQueue<List<ProductCreateRequest>> productBatchQueue;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private InitialProductLoader loader;

    private AppProperties.InitialProductLoad config;

    @BeforeEach
    void setup() {
        config = new AppProperties.InitialProductLoad();
        config.setTotal(20);
        config.setBatchSize(10);
    }

    @Test
    void shouldNotLoadIfProductsExist() throws Exception {
        when(productService.countAllProducts()).thenReturn(5L);

        loader.run(args);

        verify(categoryService, never()).getAllCategoryIds();
        verify(productBatchQueue, never()).offer(any());
    }

    @Test
    void shouldNotLoadIfNoCategories() throws Exception {
        when(productService.countAllProducts()).thenReturn(0L);
        when(categoryService.getAllCategoryIds()).thenReturn(List.of());

        loader.run(args);

        verify(productBatchQueue, never()).offer(any());
    }

    @Test
    void shouldNotLoadIfNoConfig() throws Exception {
        when(productService.countAllProducts()).thenReturn(0L);
        when(categoryService.getAllCategoryIds()).thenReturn(List.of(1L, 2L));
        when(appProperties.getInitialProductLoad()).thenReturn(null);

        loader.run(args);

        verify(productBatchQueue, never()).offer(any());
    }

    @Test
    void shouldNotLoadIfTotalZero() throws Exception {
        config.setTotal(0);

        when(productService.countAllProducts()).thenReturn(0L);
        when(categoryService.getAllCategoryIds()).thenReturn(List.of(1L, 2L));
        when(appProperties.getInitialProductLoad()).thenReturn(config);

        loader.run(args);

        verify(productBatchQueue, never()).offer(any());
    }

    @Test
    void shouldLoadProductsInBatches() throws Exception {
        when(productService.countAllProducts()).thenReturn(0L);
        when(categoryService.getAllCategoryIds()).thenReturn(List.of(1L, 2L));
        when(appProperties.getInitialProductLoad()).thenReturn(config);
        when(appProperties.getProductNames()).thenReturn(List.of());

        when(productBatchQueue.offer(any())).thenReturn(true);

        loader.run(args);

        verify(productBatchQueue, atLeastOnce()).offer(any());
    }
}
