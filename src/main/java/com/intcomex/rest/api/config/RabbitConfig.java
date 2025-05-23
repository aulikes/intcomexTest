package com.intcomex.rest.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

    private final AppProperties.EventRabbitMQ.ProductEventConfig productQueueConfig;

    public RabbitConfig(AppProperties appProperties) {
        this.productQueueConfig = appProperties.getEventRabbitMQ().getProductEventConfig();
    }

    @Bean
    public Queue productCreateQueue() {
        log.debug("Declarando cola: " + productQueueConfig.getQueueName());
        return new Queue(productQueueConfig.getQueueName(), true);
    }

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(productQueueConfig.getExchange());
    }

    @Bean
    public Binding productBinding(Queue productCreateQueue, TopicExchange productExchange) {
        return BindingBuilder.bind(productCreateQueue)
                .to(productExchange).with(productQueueConfig.getRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
