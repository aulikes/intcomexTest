package com.intcomex.rest.api.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    /**
     * Guarda la imagen y retorna la URL accesible públicamente
     */
    String storageImage(MultipartFile file, String categoryName) throws Exception;

    /**
     * Guarda la imagen usando bytes y retorna la URL accesible públicamente
     */
    String storageImage(String categoryName, String fileName, byte[] imageBytes, String contentType) throws Exception;

    /**
     * Obtiene la base url donde se guardó la imagen
     */
    String getBaseUrlImage();
}
