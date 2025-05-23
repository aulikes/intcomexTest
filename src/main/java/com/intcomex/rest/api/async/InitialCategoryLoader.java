package com.intcomex.rest.api.async;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.config.AppProperties.InitialCategory;
import com.intcomex.rest.api.exception.ImagenFormatException;
import com.intcomex.rest.api.service.contract.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

/**
 * Cargador automático de categorías iniciales al iniciar la aplicación.
 *
 * Este componente se ejecuta automáticamente al arrancar el sistema (gracias a ApplicationRunner).
 * Lee la configuración desde application.yml (imageBasePath y categorías).
 * Por cada categoría definida, carga la imagen desde el classpath y la registra usando CategoryService.
 *
 * ⚠️ En este contexto, la imagen es obligatoria para cada categoría.
 * Si falta la imagen o no se encuentra el archivo, se lanza un error y la aplicación no continúa.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class InitialCategoryLoader implements ApplicationRunner {

    private final CategoryService categoryService;
    private final AppProperties properties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<InitialCategory> categories = properties.getInitialCategories(); // Categorías definidas en YAML
        String basePath = properties.getImagesCategory().getImageBasePath(); // Ruta base donde se encuentran las imágenes

        if (categories == null || categories.isEmpty()) {
            log.info("No se definieron categorías iniciales.");
            return;
        }

        if(categoryService.existOneCategory()){
            log.info("Categorías ya existentes.");
            return;
        }

        log.info("Cargando {} categorías iniciales...", categories.size());

        for (InitialCategory category : categories) {
            ClassPathResource imageResource = getClassPathResource(category, basePath);
            try {
                // Leer el contenido binario del archivo
                byte[] imageBytes;
                try (InputStream is = imageResource.getInputStream()) {
                    imageBytes = is.readAllBytes();
                }

                // Llamar al servicio para crear la categoría
                categoryService.createCategory(
                        category.getName(),
                        category.getDescription(),
                        category.getImage(),
                        imageBytes,
                        "image/png"
                );

                log.info("Categoría '{}' cargada exitosamente.", category.getName());

            } catch (Exception e) {
                // Cualquier error es considerado crítico y se relanza
                log.error("Error al cargar la categoría '{}': {}", category.getName(), e.getMessage());
                throw new ImagenFormatException("Falló la carga inicial de categorías. La aplicación no puede continuar.", e);
            }
        }
    }

    protected ClassPathResource getClassPathResource(InitialCategory category, String basePath) {
        if (category.getImage() == null || category.getImage().isBlank()) {
            throw new IllegalStateException("La categoría '" + category.getName() + "' no tiene imagen definida.");
        }
        // Construir la ruta de la imagen
        String path = basePath + category.getImage();
        ClassPathResource imageResource = new ClassPathResource(path);

        // Validar que el archivo exista en el classpath
        if (!imageResource.exists()) {
            throw new IllegalStateException("La imagen '" + path + "' no fue encontrada en el classpath.");
        }
        return imageResource;
    }
}
