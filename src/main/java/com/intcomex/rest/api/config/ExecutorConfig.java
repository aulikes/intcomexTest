package com.intcomex.rest.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {

    private final AppProperties appProperties;

    @Bean("productBatchExecutor")
    public ThreadPoolTaskExecutor productBatchExecutor() {
        int cores = appProperties.getProductQueue().getWorkerCount() > 0
                ? appProperties.getProductQueue().getWorkerCount()
                : Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores);
        executor.setMaxPoolSize(cores);
        executor.setQueueCapacity(0); // La cola real es la de productos, no se deben agregar más
        executor.setThreadNamePrefix("product-batch-worker-");
        executor.initialize();
        return executor;
    }
}
