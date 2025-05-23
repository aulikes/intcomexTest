package com.intcomex.rest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intcomex.rest.api.config.NoSecurityTestConfig;
import com.intcomex.rest.api.dto.CategoryCreateResponse;
import com.intcomex.rest.api.service.contract.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({NoSecurityTestConfig.class, CategoryControllerTest.StubConfig.class})
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class StubConfig {
        @Bean
        @Primary
        public CategoryService categoryService() {
            return org.mockito.Mockito.mock(CategoryService.class);
        }
    }

    @Test
    @DisplayName("Debe crear una categoría correctamente con imagen y retornar 201")
    void shouldCreateCategorySuccessfully() throws Exception {
        // Imagen simulada (campo "image" en el DTO)
        MockMultipartFile mockFile = new MockMultipartFile(
                "image", "servidor.png", "image/png", "contenido".getBytes(StandardCharsets.UTF_8)
        );

        // Parámetros del formulario (DTO usa @ModelAttribute, no JSON)
        String nombre = "SERVIDORES";
        String descripcion = "Categoría para servidores físicos empresariales";

        // Mock del resultado esperado desde el servicio
        CategoryCreateResponse mockResponse = new CategoryCreateResponse();
        mockResponse.setCategoryID(1L);
        mockResponse.setCategoryName(nombre);
        mockResponse.setDescription(descripcion);
        mockResponse.setPicture("http://localhost/images/servidor.png");

        when(categoryService.createCategory(any(), any())).thenReturn(mockResponse);

        // Realizar la petición multipart con parámetros normales
        mockMvc.perform(
                        multipart("/api/categories")
                                .file(mockFile)
                                .param("categoryName", nombre)
                                .param("description", descripcion)
                                .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryID").value(1L))
                .andExpect(jsonPath("$.categoryName").value(nombre))
                .andExpect(jsonPath("$.description").value(descripcion))
                .andExpect(jsonPath("$.picture").value("http://localhost/images/servidor.png"));

        // Verifica que el servicio fue llamado
        verify(categoryService, times(1)).createCategory(any(), any());
    }

    @Test
    @DisplayName("Debe retornar 400 si falta el nombre de la categoría")
    void shouldReturn400WhenCategoryNameIsMissing() throws Exception {
        // Simulación del archivo de imagen (válido)
        MockMultipartFile mockFile = new MockMultipartFile(
                "image", "imagen.png", "image/png", "contenido".getBytes(StandardCharsets.UTF_8)
        );

        // Ejecutar la petición SIN el parámetro categoryName
        mockMvc.perform(
                        multipart("/api/categories")
                                .file(mockFile)
                                .param("description", "Categoría sin nombre") // solo se incluye la descripción
                                .characterEncoding("UTF-8")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 si la imagen no se proporciona")
    void shouldReturn400WhenImageIsMissing() throws Exception {
        // No se incluye el archivo de imagen

        mockMvc.perform(
                        multipart("/api/categories")
                                .param("categoryName", "SERVIDORES")
                                .param("description", "Categoría sin imagen")
                                .characterEncoding("UTF-8")
                )
                .andExpect(status().isBadRequest());
    }

}
