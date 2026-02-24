package com.baggage.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String BAGGAGE_CREATED_TOPIC = "baggage-created";
    public static final String BAGGAGE_UPDATED_TOPIC = "baggage-updated";
    public static final String TRACKING_UPDATED_TOPIC = "tracking-updated";
    public static final String CLAIM_CREATED_TOPIC = "claim-created";
    public static final String NOTIFICATION_TOPIC = "notification";

    @Bean
    public NewTopic baggageCreatedTopic() {
        return TopicBuilder.name(BAGGAGE_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic baggageUpdatedTopic() {
        return TopicBuilder.name(BAGGAGE_UPDATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic trackingUpdatedTopic() {
        return TopicBuilder.name(TRACKING_UPDATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic claimCreatedTopic() {
        return TopicBuilder.name(CLAIM_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(NOTIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
