package com.intcomex.rest.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imagePath = appProperties.getImagesCategory().getImagePublicPath();
        // Por si no terminan la ruta con /
        if (!imagePath.endsWith("/")) {
            imagePath = imagePath + "/";
        }
        // Debe tener el prefijo "file:"
        registry.addResourceHandler(imagePath.substring(1) + "**")  //Quitamos el punto inicial
                .addResourceLocations("file:" + imagePath);
    }
}
