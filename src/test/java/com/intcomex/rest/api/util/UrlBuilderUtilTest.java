package com.intcomex.rest.api.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlBuilderUtilTest {

    @Test
    void buildAbsoluteUrl_buildsFullUrl_whenRequestIsValid() {
        // Arrange: crea el mock del request y define sus retornos
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getScheme()).thenReturn("http");
        when(mockRequest.getServerName()).thenReturn("localhost");
        when(mockRequest.getServerPort()).thenReturn(8096);

        // Act
        String url = UrlBuilderUtil.buildAbsoluteUrl(mockRequest) + "/images/categories/imagen.png";

        // Assert
        assertEquals("http://localhost:8096/images/categories/imagen.png", url);
    }

    @Test
    void buildAbsoluteUrl_throwsException_whenRequestIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                UrlBuilderUtil.buildAbsoluteUrl(null)
        );
    }
}

