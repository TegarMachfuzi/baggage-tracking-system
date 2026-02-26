package com.passenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@EnableCaching
public class PassengerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassengerServiceApplication.class, args);
    }
}
