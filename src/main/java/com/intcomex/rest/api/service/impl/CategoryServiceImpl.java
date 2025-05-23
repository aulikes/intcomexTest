package com.intcomex.rest.api.service.impl;

import com.intcomex.rest.api.dto.CategoryCreateRequest;
import com.intcomex.rest.api.dto.CategoryCreateResponse;
import com.intcomex.rest.api.entity.Category;
import com.intcomex.rest.api.exception.BusinessException;
import com.intcomex.rest.api.mapper.CategoryMapper;
import com.intcomex.rest.api.repository.CategoryRepository;
import com.intcomex.rest.api.service.contract.CategoryService;
import com.intcomex.rest.api.service.storage.ImageStorageService;
import com.intcomex.rest.api.util.UrlBuilderUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ImageStorageService imageStorageService;

    @Transactional(readOnly = true)
    public List<Long> getAllCategoryIds() {
        return categoryRepository.findAll().stream().map(Category::getCategoryID).toList();
    }

    /**
     * Crea Categorías desde el EndPoint
     */
    @Override
    @Transactional
    public CategoryCreateResponse createCategory(CategoryCreateRequest categoryCreateRequest,
                                                 HttpServletRequest httpServletRequest) {
        Category savedCategory = createCategoryInternal(
            categoryCreateRequest.getCategoryName(),
            categoryCreateRequest.getDescription(),
            catName -> {
                try {
                    return imageStorageService.storageImage(categoryCreateRequest.getImage(), catName);
                } catch (Exception e) {
                    throw new BusinessException("Error al guardar la imagen: " + e.getMessage(), e);
                }
            }
        );
        CategoryCreateResponse dto = categoryMapper.toDTO(savedCategory);
        dto.setPicture(imageStorageService.getBaseUrlImage() + savedCategory.getPicture());
        return dto;
    }

    @Override
    @Transactional
    public CategoryCreateResponse createCategory(String categoryName, String description,
                                                 String fileName, byte[] imageBytes, String contentType) {
        // Usa el método unificado con un lambda para la imagen en bytes
        Category savedCategory = createCategoryInternal(
            categoryName,
            description,
            catName -> {
                try {
                    return imageStorageService.storageImage(catName, fileName, imageBytes, contentType);
                } catch (Exception e) {
                    throw new BusinessException("Error al guardar la imagen: " + e.getMessage(), e);
                }
            }
        );
        return categoryMapper.toDTO(savedCategory);
    }

    public boolean existOneCategory(){
        return categoryRepository.count() > 0;
    }

    /**
     * Centraliza el flujo de creación de categoría y almacenamiento de imagen.
     */
    private Category createCategoryInternal(
            String categoryName,
            String description,
            Function<String, String> imageSaver) {

        if (categoryRepository.existsByCategoryNameIgnoreCase(categoryName)) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + categoryName);
        }

        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setDescription(description);
        category.setPicture(null);
        Category savedCategory = categoryRepository.save(category);
        log.debug(">>>>> Categoría guardada: {}", savedCategory);

        try {
            log.debug(">>>>> Categoría Inicio LambdaFunction: {}", categoryName);
            String imageUrl = imageSaver.apply(categoryName);
            log.debug(">>>>> Categoría Picture actualizada: {}", imageUrl);
            savedCategory.setPicture(imageUrl);
            categoryRepository.save(savedCategory);
            log.debug(">>>>> Categoría actualizada: {}", savedCategory);
            return savedCategory;
        } catch (Exception ex) {
            throw new BusinessException("Error al guardar la imagen. No se creó la categoría.");
        }
    }
}
