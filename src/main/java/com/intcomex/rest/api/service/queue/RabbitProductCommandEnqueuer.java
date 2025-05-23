package com.intcomex.rest.api.service.queue;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.dto.ProductCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("prod")
public class RabbitProductCommandEnqueuer implements ProductCommandEnqueuer {

    private final RabbitTemplate rabbitTemplate;
    private final AppProperties.EventRabbitMQ.ProductEventConfig config;

    public RabbitProductCommandEnqueuer(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = appProperties.getEventRabbitMQ().getProductEventConfig();
    }

    @Override
    public void enqueue(ProductCreateRequest request) {
        rabbitTemplate.convertAndSend(config.getExchange(), config.getRoutingKey(), request);
        log.debug("Producto encolado en RabbitMQ: {}", request.getProductName());
    }
}
