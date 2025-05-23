package com.intcomex.rest.api.async;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.config.AppProperties.InitialCategory;
import com.intcomex.rest.api.exception.ImagenFormatException;
import com.intcomex.rest.api.service.contract.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitialCategoryLoaderTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.ImagesCategory imagesCategory;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private InitialCategoryLoader loader;

    private InitialCategory category;

    @BeforeEach
    void setUp() {
        category = new InitialCategory();
        category.setName("SERVIDORES");
        category.setDescription("Categoría para servidores físicos empresariales");
        category.setImage("servidor-api.png");
    }

    @Test
    void shouldNotLoadIfNoCategoriesDefined() throws Exception {
        when(appProperties.getInitialCategories()).thenReturn(List.of());
        when(appProperties.getImagesCategory()).thenReturn(imagesCategory);
        when(imagesCategory.getImageBasePath()).thenReturn("");

        loader.run(args);

        verify(categoryService, never()).createCategory(any(), any(), any(), any(), any());
    }

    @Test
    void shouldLoadSingleCategorySuccessfully() throws Exception {
        String fakeImagePath = "images/initial/servidor-api.png";
        byte[] fakeImageBytes = "fake-image-bytes".getBytes();

        when(appProperties.getInitialCategories()).thenReturn(List.of(category));
        when(appProperties.getImagesCategory()).thenReturn(imagesCategory);
        when(imagesCategory.getImageBasePath()).thenReturn("images/initial/");

        // Mock de ClassPathResource
        ClassPathResource resource = spy(new ClassPathResource(fakeImagePath));
        doReturn(new ByteArrayInputStream(fakeImageBytes)).when(resource).getInputStream();

        InitialCategoryLoader testLoader = new InitialCategoryLoader(categoryService, appProperties) {
            @Override
            protected ClassPathResource getClassPathResource(InitialCategory category, String basePath) {
                return resource;
            }
        };

        testLoader.run(args);

        verify(categoryService).createCategory(
                eq("SERVIDORES"),
                eq("Categoría para servidores físicos empresariales"),
                eq("servidor-api.png"),
                eq(fakeImageBytes),
                eq("image/png")
        );
    }

    @Test
    void shouldFailIfImageNotExists() {
        when(appProperties.getInitialCategories()).thenReturn(List.of(category));
        when(appProperties.getImagesCategory()).thenReturn(imagesCategory);
        when(imagesCategory.getImageBasePath()).thenReturn("images/initial/");

        InitialCategoryLoader testLoader = new InitialCategoryLoader(categoryService, appProperties) {
            @Override
            protected ClassPathResource getClassPathResource(InitialCategory category, String basePath) {
                return new ClassPathResource("non-existent.png");
            }
        };

        assertThrows(ImagenFormatException.class, () -> testLoader.run(args));
    }
}
