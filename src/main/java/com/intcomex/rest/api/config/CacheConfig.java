package com.intcomex.rest.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache para páginas de productos.
     * TTL de 10 segundos, máximo 100 entradas.
     */
    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager("productsPage", "getProductID");
        cm.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10))
                .maximumSize(100));
        return cm;
    }
}
