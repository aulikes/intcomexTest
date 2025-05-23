package com.intcomex.rest.api.async;

import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.service.contract.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RabbitProductCommandListener {

    private final ProductService productService;

//    @RabbitListener(queues = "product.create.queue")
//    public void handleProductCreate(ProductCreateRequest request) {
//        log.info("Recibido producto desde RabbitMQ: {}", request.getProductName());
//        productService.createProduct(Collections.singletonList(request));
//    }

    @RabbitListener(queues = "product.create.queue", concurrency = "5-20")
    public void handleProduct(ProductCreateRequest request) {
        log.debug("Recibido producto desde RabbitMQ: {}", request.getProductName());
        productService.createProduct(List.of(request));
    }
}
