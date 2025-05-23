package com.intcomex.rest.api.service.storage;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.util.UrlBuilderUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Profile("dev")
public class LocalImageStorageServiceImpl extends AbstractImageStorageService {

    private final HttpServletRequest request;

    public LocalImageStorageServiceImpl(AppProperties appProperties, HttpServletRequest request) {
        super(appProperties);
        this.request = request;
    }

    @Override
    public String storageImage(MultipartFile multipartFile, String categoryName) throws IOException {
        byte[] imageBytes = multipartFile.getBytes();
        String fileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        return storageImage(categoryName, fileName, imageBytes, contentType);
    }

    @Override
    public String storageImage(String categoryName, String fileName, byte[] imageBytes, String contentType) throws IOException {
        validateImage(imageBytes, contentType);
        String uniqueName = buildUniqueFileName(categoryName, fileName);
        String basePath = appProperties.getImagesCategory().getImagePublicPath(); //  ./images/categories/
        Path categoryDir = Paths.get(basePath);
        writeToDisk(categoryDir, imageBytes, uniqueName);
        return (basePath.startsWith("./") ? basePath.substring(1) : basePath) + uniqueName;
    }

    protected void writeToDisk(Path categoryDir, byte[] imageBytes, String uniqueName) throws IOException {
        Path imagePath = categoryDir.resolve(uniqueName);
        Files.createDirectories(categoryDir);
        Files.write(imagePath, imageBytes);
    }

    public String getBaseUrlImage(){
        return UrlBuilderUtil.buildAbsoluteUrl(request);
    }
}

