package com.intcomex.rest.api.service;

import com.intcomex.rest.api.dto.*;
import com.intcomex.rest.api.entity.Product;
import com.intcomex.rest.api.entity.Category;
import com.intcomex.rest.api.exception.ResourceNotFoundException;
import com.intcomex.rest.api.mapper.ProductReqMapper;
import com.intcomex.rest.api.mapper.ProductResMapper;
import com.intcomex.rest.api.repository.CategoryRepository;
import com.intcomex.rest.api.repository.ProductRepository;
import com.intcomex.rest.api.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductReqMapper productReqMapper;

    @Mock
    private ProductResMapper productResMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductCreateRequest createRequest;
    private ProductGetResponse getResponse;
    private Category category;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setCategoryID(1L);
        category.setCategoryName("Hardware");

        product = new Product();
        product.setProductID(1L);
        product.setProductName("Laptop");
        product.setUnitPrice(new BigDecimal("1500"));
        product.setCategory(category);

        createRequest = new ProductCreateRequest();
        createRequest.setProductName("Laptop");
        createRequest.setUnitPrice(new BigDecimal("1500"));
        createRequest.setQuantityPerUnit("1 unidad");
        createRequest.setUnitsInStock(10);
        createRequest.setUnitsOnOrder(2);
        createRequest.setCategoryID(1L);

        getResponse = new ProductGetResponse();
        getResponse.setProductID(1L);
        getResponse.setProductName("Laptop");
        getResponse.setUnitPrice(new BigDecimal("1500"));
    }

    @Test
    void countAllProducts_shouldReturnTotal() {
        when(productRepository.count()).thenReturn(5L);
        long count = productService.countAllProducts();
        assertEquals(5L, count);
    }

    @Test
    void getAllProductsPaginated_shouldReturnPage() {
        PaginationRequest pagination = new PaginationRequest();
        pagination.setPage(0);
        pagination.setSize(10);
        pagination.setWithTotal(true);

        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(productResMapper.toDTO(any(Product.class))).thenReturn(getResponse);

        PaginationResponse<ProductGetResponse> result = productService.getAllProductsPaginated(pagination);

        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().getFirst().getProductName());
    }

    @Test
    void getProductById_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productResMapper.toDTO(any(Product.class))).thenReturn(getResponse);

        ProductGetResponse result = productService.getProductById(1L);

        assertEquals("Laptop", result.getProductName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_notFound_shouldThrow() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_shouldSaveSuccessfully() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productReqMapper.toEntity(createRequest)).thenReturn(product);

        productService.createProduct(List.of(createRequest));
    }

    @Test
    void createProduct_invalidCategory_shouldThrow() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(List.of(createRequest)));
    }
}
