package com.intcomex.rest.api.service.storage;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.config.AppProperties.ImagesCategory;
import com.intcomex.rest.api.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalImageStorageServiceImplTest {

    private LocalImageStorageServiceImpl storageSpy;

    @Mock
    private AppProperties appProperties;

    private final ImagesCategory imagesCategory = new ImagesCategory();

    @BeforeEach
    void setUp() throws IOException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        imagesCategory.setImagePublicPath("/fake/base/path/"); // Ruta fake
        imagesCategory.setFormatImages(List.of("image/png", "image/jpeg"));
        imagesCategory.setSizeImageCat(10485760); // 10MB

        when(appProperties.getImagesCategory()).thenReturn(imagesCategory);

        // Crear instancia real y convertirla en spy
        LocalImageStorageServiceImpl realService = new LocalImageStorageServiceImpl(appProperties, mockRequest);
        storageSpy = spy(realService);
    }

    @Test
    void shouldSaveImageFromMultipartFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "imagen.png", "image/png", "contenido".getBytes()
        );
        // Evitar escritura en disco real
        doNothing().when(storageSpy).writeToDisk(any(), any(), any());

        String result = storageSpy.storageImage(file, "SERVIDORES");
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);

        assertNotNull(result);
        assertTrue(result.startsWith(imagesCategory.getImagePublicPath()));
        assertTrue(result.endsWith(".png"));
        verify(storageSpy, times(1)).writeToDisk(any(), any(), any());
    }

    @Test
    void shouldSaveImageFromRawBytes() throws IOException {
        byte[] content = "imagen".getBytes();
        // Evitar escritura en disco real
        doNothing().when(storageSpy).writeToDisk(any(), any(), any());

        String result = storageSpy.storageImage("REDES", "switch.png", content, "image/png");
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + result);

        assertNotNull(result);
        assertTrue(result.startsWith(imagesCategory.getImagePublicPath()));
        assertTrue(result.endsWith(".png"));
        verify(storageSpy, times(1)).writeToDisk(any(), any(), any());
    }

    @Test
    void shouldFailIfContentTypeInvalid() throws IOException {
        byte[] content = "pdf".getBytes();

        BusinessException ex = assertThrows(BusinessException.class, () ->
                storageSpy.storageImage("DOCS", "manual.pdf", content, "application/pdf")
        );

        verify(storageSpy, never()).writeToDisk(any(), any(), any());
    }

    @Test
    void shouldFailIfExtensionNotAllowed() throws IOException {
        byte[] content = "imagen".getBytes();
        imagesCategory.setFormatImages(List.of("image/jpeg")); // Solo jpg

        BusinessException ex = assertThrows(BusinessException.class, () ->
                storageSpy.storageImage("BLOQUEADOS", "archivo.png", content, "image/png")
        );

        verify(storageSpy, never()).writeToDisk(any(), any(), any());
    }
}
