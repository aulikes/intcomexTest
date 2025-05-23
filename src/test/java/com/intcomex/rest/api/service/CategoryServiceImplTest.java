package com.intcomex.rest.api.service;

import com.intcomex.rest.api.dto.CategoryCreateRequest;
import com.intcomex.rest.api.dto.CategoryCreateResponse;
import com.intcomex.rest.api.entity.Category;
import com.intcomex.rest.api.exception.BusinessException;
import com.intcomex.rest.api.mapper.CategoryMapper;
import com.intcomex.rest.api.repository.CategoryRepository;
import com.intcomex.rest.api.service.impl.CategoryServiceImpl;
import com.intcomex.rest.api.service.storage.ImageStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @SneakyThrows
    @Test
    void createCategorySuccess() {
        // Arrange
        String categoryName = "Laptops";
        String description = "Portátiles";
        String picture = "images/categories/laptops-1.png";
        String imageUrl = "http://localhost:8080/"+picture;
        MultipartFile mockFile = mock(MultipartFile.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setCategoryName(categoryName);
        request.setDescription(description);
        request.setImage(mockFile);

        Category categoryEntity = new Category();
        categoryEntity.setCategoryName(categoryName);
        categoryEntity.setDescription(description);

        Category savedCategory = new Category();
        savedCategory.setCategoryName(categoryName);
        savedCategory.setDescription(description);
        savedCategory.setPicture(imageUrl);

        CategoryCreateResponse response = new CategoryCreateResponse();
        response.setCategoryName(categoryName);
        response.setDescription(description);
        response.setPicture(imageUrl);

        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryEntity, savedCategory);
        when(imageStorageService.storageImage(mockFile, categoryName)).thenReturn(picture);
        when(imageStorageService.getBaseUrlImage()).thenReturn("http://localhost:8080/");
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(response);

        // Act
        CategoryCreateResponse result = categoryService.createCategory(request, mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(categoryName, result.getCategoryName());
        assertEquals(imageUrl, result.getPicture());
        verify(categoryRepository, times(2)).save(any(Category.class));
        verify(imageStorageService, times(1)).storageImage(mockFile, categoryName);
        verify(categoryMapper, times(1)).toDTO(any(Category.class));
    }

    @SneakyThrows
    @Test
    void createCategoryDuplicateName() {
        String categoryName = "Duplicada";
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setCategoryName(categoryName);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(true);

        assertThrows(BusinessException.class, () -> categoryService.createCategory(request, mockRequest));
        verify(categoryRepository, never()).save(any(Category.class));
        verify(imageStorageService, never()).storageImage(any(), any());
        verify(categoryMapper, never()).toDTO(any());
    }

    @SneakyThrows
    @Test
    void createCategoryImageStorageFails() {
        String categoryName = "Impresoras";
        String description = "Impresoras rápidas";
        MultipartFile mockFile = mock(MultipartFile.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setCategoryName(categoryName);
        request.setDescription(description);
        request.setImage(mockFile);

        Category categoryEntity = new Category();
        categoryEntity.setCategoryName(categoryName);
        categoryEntity.setDescription(description);

        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryEntity);
        when(imageStorageService.storageImage(mockFile, categoryName))
                .thenThrow(new RuntimeException("Error al guardar imagen"));

        assertThrows(BusinessException.class, () -> categoryService.createCategory(request, mockRequest));
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(imageStorageService, times(1)).storageImage(mockFile, categoryName);
        verify(categoryMapper, never()).toDTO(any());
    }

    @SneakyThrows
    @Test
    void createCategoryOverloadSuccess() {
        // Arrange
        String categoryName = "Accesorios";
        String description = "Varios accesorios";
        String fileName = "accesorio.jpg";
        byte[] imageBytes = new byte[]{1, 2, 3};
        String contentType = "image/jpeg";
        String picture = "images/categories/accesorio-xyz.jpg";
        String imageUrl = "http://localhost:8096/" + picture;

        Category categoryEntity = new Category();
        categoryEntity.setCategoryName(categoryName);
        categoryEntity.setDescription(description);

        Category savedCategory = new Category();
        savedCategory.setCategoryName(categoryName);
        savedCategory.setDescription(description);
        savedCategory.setPicture(imageUrl);

        CategoryCreateResponse response = new CategoryCreateResponse();
        response.setCategoryName(categoryName);
        response.setDescription(description);
        response.setPicture(imageUrl);

        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryEntity, savedCategory);
        when(imageStorageService.storageImage(categoryName, fileName, imageBytes, contentType)).thenReturn(picture);
        when(categoryMapper.toDTO(any(Category.class))).thenReturn(response);

        // Act
        CategoryCreateResponse result = categoryService.createCategory(
                categoryName, description, fileName, imageBytes, contentType);

        // Assert
        assertNotNull(result);
        assertEquals(categoryName, result.getCategoryName());
        assertEquals(imageUrl, result.getPicture());
        verify(categoryRepository, times(2)).save(any(Category.class));
        verify(imageStorageService, times(1)).storageImage(categoryName, fileName, imageBytes, contentType);
        verify(categoryMapper, times(1)).toDTO(any(Category.class));
    }

    @SneakyThrows
    @Test
    void createCategoryOverloadDuplicateName() {
        String categoryName = "Duplicada";
        String description = "Desc";
        String fileName = "dup.png";
        byte[] imageBytes = new byte[]{1, 2, 3};
        String contentType = "image/png";

        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                categoryService.createCategory(categoryName, description, fileName, imageBytes, contentType)
        );
        verify(categoryRepository, never()).save(any(Category.class));
        verify(imageStorageService, never()).storageImage(anyString(), anyString(), any(), anyString());
        verify(categoryMapper, never()).toDTO(any());
    }

    @SneakyThrows
    @Test
    void createCategoryOverloadImageStorageFails() {
        String categoryName = "Monitores";
        String description = "Pantallas";
        String fileName = "monitor.png";
        byte[] imageBytes = new byte[]{1, 2, 3};
        String contentType = "image/png";

        Category categoryEntity = new Category();
        categoryEntity.setCategoryName(categoryName);
        categoryEntity.setDescription(description);

        when(categoryRepository.existsByCategoryNameIgnoreCase(categoryName)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryEntity);
        when(imageStorageService.storageImage(categoryName, fileName, imageBytes, contentType))
                .thenThrow(new RuntimeException("Error al guardar imagen"));

        assertThrows(BusinessException.class, () ->
                categoryService.createCategory(categoryName, description, fileName, imageBytes, contentType)
        );
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(imageStorageService, times(1)).storageImage(categoryName, fileName, imageBytes, contentType);
        verify(categoryMapper, never()).toDTO(any());
    }
}
