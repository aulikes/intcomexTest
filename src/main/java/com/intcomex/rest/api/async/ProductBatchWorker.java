package com.intcomex.rest.api.async;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.config.AppProperties.ProductQueueConfig;
import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.service.contract.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class ProductBatchWorker {

    private final BlockingQueue<List<ProductCreateRequest>> productBatchQueue;
    private final ProductService productService;
    private final ProductQueueConfig productQueueConfig;
    private final ThreadPoolTaskExecutor productBatchExecutor;

    public ProductBatchWorker(
            BlockingQueue<List<ProductCreateRequest>> productBatchQueue,
            ProductService productService, AppProperties appProperties,
            ThreadPoolTaskExecutor productBatchExecutor) {
        this.productBatchQueue = productBatchQueue;
        this.productService = productService;
        this.productQueueConfig = appProperties.getProductQueue();
        this.productBatchExecutor = productBatchExecutor;
    }

    @PostConstruct
    public void startWorkers() {
        int workerCount = productQueueConfig.getWorkerCount() > 0
                ? productQueueConfig.getWorkerCount()
                : Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < workerCount; i++) {
            productBatchExecutor.execute(() -> {
                while (true) {
                    try {
                        List<ProductCreateRequest> batch = productBatchQueue.take();
                        productService.createProduct(batch);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception ex) {
                        log.error("Error procesando lote de productos", ex);
                    }
                }
            });
        }
    }
}
