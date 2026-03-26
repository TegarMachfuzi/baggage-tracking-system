package com.baggage.claim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@ComponentScan(basePackages = {"com.baggage.claim", "com.baggage.config"})
@EntityScan(basePackages = {"com.baggage.model", "com.baggage.claim"})
public class ClaimServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaimServiceApplication.class, args);
    }
}
