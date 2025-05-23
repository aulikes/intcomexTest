package com.intcomex.rest.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Mapea todas las propiedades definidas en application.yml bajo el prefijo "app".
 * Centraliza la configuración del sistema para facilitar mantenimiento y ajustes.
 */
@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Zona horaria para personalizar fechas
     */
    private CustomerZone customerZone = new CustomerZone();

    /**
     * Zona horaria para personalizar fechas
     */
    private ImagesCategory imagesCategory = new ImagesCategory();

    /**
     * Lista de categorías que deben crearse automáticamente al iniciar el sistema.
     */
    private List<InitialCategory> initialCategories;

    /**
     * Parámetros para la carga masiva inicial de productos (cantidad total, tamaño de batch).
     */
    private InitialProductLoad initialProductLoad;

    /**
     * Opciones de nombres de productos, por categoría: tipos y modelos disponibles.
     */
    private List<ProductNameOptions> productNames;

    /**
     * Configuración para la cola de productos asincrónica en memoria.
     */
    private ProductQueueConfig productQueue = new ProductQueueConfig();

    /**
     * Configuración del JWT
     */
    private JwtProperties jwt = new JwtProperties();

    /**
     * Configuración del AWS
     */
    private AwsProperties aws;

    /**
     * Configuración del RABBIT
     */
    private EventRabbitMQ eventRabbitMQ;

    // -----------------------------------------------
    // Subclases anidadas
    // -----------------------------------------------

    @Data
    public static class CustomerZone {
        private String timezone = "UTC";            // Zona por defecto
        private String headerComponent = "X-Timezone"; // Nombre del header por defecto
    }

    @Data
    public static class ImagesCategory {
        private String imagePublicPath;
        private String imageBasePath;
        private int sizeImageCat = 10485760;
        private List<String> formatImages;
    }

    @Data
    public static class InitialCategory {
        private String name;
        private String description;
        private String image;
    }

    @Data
    public static class InitialProductLoad {
        private int total;
        private int batchSize;
    }

    @Data
    public static class ProductNameOptions {
        private List<String> types;
        private List<String> models;
    }

    @Data
    public static class ProductQueueConfig {
        private int batchSize;      // Máx. productos por lote
        private int queueCapacity;  // Máx. lotes en cola
        private int workerCount;    // Nº de workers
    }

    @Data
    public static class ProductBatchConfig {
        private int size;
        private long fixedDelayMs;
    }

    @Data
    public static class JwtProperties {
        private long expirationMillis = 3600000; // valor por defecto: 1 hora
    }

    @Data
    public static class AwsProperties {
        private S3Properties s3;

        @Data
        public static class S3Properties {
            private String bucket;
            private String region;
            private String accessKeyId;
            private String secretAccessKey;
            private String urlBase;
        }
    }

    @Data
    public static class EventRabbitMQ {
        private ProductEventConfig productEventConfig;

        @Data
        public static class ProductEventConfig {
            private String queueName;
            private String exchange;
            private String routingKey;
        }
    }
}
