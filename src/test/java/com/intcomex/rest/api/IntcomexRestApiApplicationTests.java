package com.intcomex.rest.api;

import com.intcomex.rest.api.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableConfigurationProperties(AppProperties.class)
class IntcomexRestApiApplicationTests {

	@Test
	    void contextLoads() {
	}

}
