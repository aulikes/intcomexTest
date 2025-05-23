package com.intcomex.rest.api.service.storage;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractImageStorageService implements ImageStorageService {

    protected final AppProperties appProperties;

    protected void validateImage(byte[] imageBytes, String contentType) {
        int sizeImageCat = appProperties.getImagesCategory().getSizeImageCat();
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BusinessException("Debe existir una imagen a crear");
        }
        if (imageBytes.length > sizeImageCat) {
            throw new BusinessException("El tamaño de la imagen supera el límite permitido (" + sizeImageCat + " bytes).");
        }
        List<String> formatImages = appProperties.getImagesCategory().getFormatImages();
        if (!formatImages.contains(contentType)) {
            throw new BusinessException("Formato de imagen no soportado. Formatos permitidos: " + formatImages);
        }
    }

    protected String buildUniqueFileName(String categoryName, String fileName) {
        String ext = getExtension(fileName);
        return categoryName + "-" + UUID.randomUUID() + ext;
    }

    protected String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
