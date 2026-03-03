package com.baggage.claim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.baggage.claim", "com.baggage.config"})
public class ClaimServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ClaimServiceApplication.class, args);
    }
}
