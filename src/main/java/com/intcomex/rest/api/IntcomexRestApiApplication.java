package com.intcomex.rest.api;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.config.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties({AppProperties.class, AppSecurityProperties.class})
public class IntcomexRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntcomexRestApiApplication.class, args);
	}

}
