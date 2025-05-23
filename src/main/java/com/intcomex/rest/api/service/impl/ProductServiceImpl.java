package com.intcomex.rest.api.service.impl;

import com.intcomex.rest.api.dto.*;
import com.intcomex.rest.api.entity.Product;
import com.intcomex.rest.api.exception.ResourceNotFoundException;
import com.intcomex.rest.api.mapper.ProductReqMapper;
import com.intcomex.rest.api.mapper.ProductResMapper;
import com.intcomex.rest.api.repository.CategoryRepository;
import com.intcomex.rest.api.repository.ProductRepository;
import com.intcomex.rest.api.service.contract.ProductService;
import com.intcomex.rest.api.service.storage.ImageStorageService;
import com.intcomex.rest.api.util.UrlBuilderUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReqMapper productReqMapper;
    private final ProductResMapper productResMapper;
    private final ImageStorageService imageStorageService;
    private final HttpServletRequest request;


    @Override
    @Transactional(readOnly = true)
    public long countAllProducts(){
        return productRepository.count();
    }

    /**
     * Retorna una página de productos con todos los metadatos de paginación.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productsPage",
            key = "#pagReq.page + '-' + #pagReq.size + '-' + #pagReq.withTotal + '-' " +
                    "+ (#pagReq.sortBy == null ? '' : #sortBy) + '-' " +
                    "+ (#pagReq.direction == null ? '' : #pagReq.direction)"
    )
    public PaginationResponse<ProductGetResponse> getAllProductsPaginated(PaginationRequest pagReq) {
        Pageable pageable = pagReq.toPageable();

        if (pagReq.isWithTotal()) {
            Page<Product> page = productRepository.findAll(pageable);
            List<ProductGetResponse> content = page.getContent()
                    .stream()
                    .map(this::getProductImage)
                    .collect(Collectors.toList());

            return PaginationResponse.<ProductGetResponse>builder()
                    .typeInterface("Page")
                    .content(content)
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .hasNext(page.hasNext())
                    .build();
        } else {
            Slice<Product> slice = productRepository.findAll(pageable);
            List<ProductGetResponse> content = slice.getContent()
                    .stream()
                    .map(this::getProductImage)
                    .toList();

            return PaginationResponse.<ProductGetResponse>builder()
                    .typeInterface("Slice")
                    .content(content)
                    .page(slice.getNumber())
                    .size(slice.getSize())
                    .hasNext(slice.hasNext())
                    .build();
        }
    }

    /**
     * Obtener producto por ID, incluyendo la imagen de su categoría asociada.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "getProductID", key = "#id")
    public ProductGetResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return getProductImage(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productsPage","getProductID"}, allEntries = true)
    public void createProduct(List<ProductCreateRequest> productList) {
        List<Product> productsToSave = productList.stream()
                .map(request -> {
                    var categoryOpt = categoryRepository.findById(request.getCategoryID());
                    if (categoryOpt.isEmpty()) {
                        log.warn("Categoría no encontrada para producto: {}", request);
                        throw new ResourceNotFoundException("Categoría no encontrada: " + request.getCategoryID());
                    }
                    Product product = productReqMapper.toEntity(request);
                    product.setCategory(categoryOpt.get());
                    return product;
                })
                .collect(Collectors.toList());
        productRepository.saveAll(productsToSave);
        log.info("Procesado y guardado lote de {} productos", productsToSave.size());
    }

    private ProductGetResponse getProductImage(Product product){
        ProductGetResponse dto = productResMapper.toDTO(product);
        // Lógica adicional para setear la imagen base64
        if (product.getCategory() != null){
            dto.setCategoryID(product.getCategory().getCategoryID());
            dto.setCategoryName(product.getCategory().getCategoryName());
            if(product.getCategory().getPicture() != null){
                dto.setCategoryImage(imageStorageService.getBaseUrlImage() + product.getCategory().getPicture());
            }
        }
        return dto;
    }
}
