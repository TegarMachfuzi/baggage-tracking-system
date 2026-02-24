package com.baggage.kafka.consume;

import com.baggage.kafka.biz.IConsumeDataBiz;
import com.baggage.kafka.consume.kafkahelper.IKafkaHelperMessageReceive;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaBaggageConsumer implements IKafkaHelperMessageReceive<String> {

    @Autowired
    IConsumeDataBiz consumeDataBiz;

    private ObjectMapper objectMapper;

    @Autowired
    public KafkaBaggageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topics = "sendTopic", groupId = "test", containerFactory = "kafkaListenerContainerFactory")
    public void receive(String message) {
        try {
            consumeDataBiz.processConsumeDataVeggie();
        }
    }
}
