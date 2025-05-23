package com.intcomex.rest.api.async;

import com.intcomex.rest.api.dto.ProductCreateRequest;
import com.intcomex.rest.api.service.contract.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class RabbitProductCommandListenerIT {
//
//    @Container
//    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.9.11");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
//        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
//    }
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @SpyBean
//    private ProductService productService;
//
//    @Test
//    void shouldProcess20MessagesFromRabbitMQ() {
//        int cant = 5;
//        // Enviar 20 productos diferentes
//        for (int i = 1; i <= cant; i++) {
//            ProductCreateRequest request = createValidRequest(i);
//            rabbitTemplate.convertAndSend("product.exchange", "product.create", request);
//        }
//
//        // Esperar que el listener lo procese (máximo 5 segundos)
//        await().atMost(cant, SECONDS).untilAsserted(() -> {
//            ArgumentCaptor<List<ProductCreateRequest>> captor = ArgumentCaptor.forClass(List.class);
//            verify(productService, timeout(5000).atLeast(1)).createProduct(captor.capture());
//
//            // Unir todos los productos procesados
//            List<ProductCreateRequest> total = new ArrayList<>();
//            captor.getAllValues().forEach(total::addAll);
//
//            assertThat(total).hasSize(20);
//            assertThat(total.stream().map(ProductCreateRequest::getProductName)).contains("Producto #1", "Producto #20");
//        });
//    }
//
//
//    @Test
//    void shouldProcessMessageFromRabbitMQ() {
//        ProductCreateRequest request = createValidRequest(1);
//        // Enviar mensaje
//        rabbitTemplate.convertAndSend("product.exchange", "product.create", request);
//
//        // Esperar que el listener lo procese (hasta 5 segundos)
//        await().atMost(5, SECONDS).untilAsserted(() -> {
//            ArgumentCaptor<List<ProductCreateRequest>> captor = ArgumentCaptor.forClass(List.class);
//            verify(productService).createProduct(captor.capture());
//
//            List<ProductCreateRequest> lote = captor.getValue();
//            assertThat(lote).hasSize(1);
//            assertThat(lote.get(0).getProductName()).isEqualTo("Servidor Pro X500");
//        });
//    }
//
//    private ProductCreateRequest createValidRequest(int i) {
//        ProductCreateRequest request = new ProductCreateRequest();
//        request.setProductName("Producto #" + i);
//        request.setQuantityPerUnit("1 unidad");
//        request.setUnitPrice(BigDecimal.valueOf(1500 + i));
//        request.setUnitsInStock(10 + i);
//        request.setUnitsOnOrder(i);
//        request.setReorderLevel(5);
//        request.setDiscontinued(false);
//        request.setCategoryID(1L);
//        request.setProveedorID(2L);
//        return request;
//    }
}
