package com.baggage.kafka.consume.kafkahelper;

public interface IKafkaHelperMessageReceive<String> {
    void receive(String message);
}
