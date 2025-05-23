package com.intcomex.rest.api.config;

import com.intcomex.rest.api.dto.ProductCreateRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Configuration
public class QueueConfig {

    private final AppProperties.ProductQueueConfig productQueueConfig;

    public QueueConfig(AppProperties appProperties) {
        this.productQueueConfig = appProperties.getProductQueue();
    }

    @Bean
    public BlockingQueue<List<ProductCreateRequest>> productBatchQueue() {
        // Tamaño sugerido: 5000 lotes en cola. Puedes ajustar este número.
        return new ArrayBlockingQueue<>(productQueueConfig.getQueueCapacity());
    }
}
