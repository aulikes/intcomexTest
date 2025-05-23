package com.intcomex.rest.api.service.queue;

import com.intcomex.rest.api.dto.ProductCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
@Profile("dev")
public class BufferedProductCommandEnqueuer implements ProductCommandEnqueuer {

    private final BlockingQueue<List<ProductCreateRequest>> productBatchQueue;
    private final List<ProductCreateRequest> buffer = new ArrayList<>();

    public BufferedProductCommandEnqueuer(BlockingQueue<List<ProductCreateRequest>> productBatchQueue) {
        this.productBatchQueue = productBatchQueue;
    }

    @Override
    public synchronized void enqueue(ProductCreateRequest request) {
        buffer.add(request);

        int batchSize = 100;
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    @Scheduled(fixedDelay = 100)
    public synchronized void flushByTimer() {
        if (!buffer.isEmpty()) {
            flush();
        }
    }

    private void flush() {
        try {
            productBatchQueue.put(new ArrayList<>(buffer));
            log.debug("Lote encolado desde buffer interno ({} productos)", buffer.size());
            buffer.clear();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error al encolar lote en memoria: {}", e.getMessage());
        }
    }
}
