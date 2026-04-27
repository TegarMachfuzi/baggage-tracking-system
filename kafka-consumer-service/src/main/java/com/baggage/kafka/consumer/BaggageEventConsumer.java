package com.baggage.kafka.consumer;

import com.baggage.dto.event.BaggageEventDto;
import com.baggage.kafka.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BaggageEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(BaggageEventConsumer.class);
    
    @Autowired
    private CacheService cacheService;
    
    @KafkaListener(topics = "baggage-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBaggageCreated(BaggageEventDto event) {
        log.info("Received baggage-created event: baggageId={}, barcode={}", 
                event.getBaggageId(), event.getBarcode());
        
        try {
            // Invalidate stale cache so baggage-service re-caches with correct BaggageResDto
            cacheService.invalidate("baggage:" + event.getBaggageId());
            cacheService.invalidate("baggage:barcode:" + event.getBarcode());
            
            log.info("Successfully processed baggage-created event");
        } catch (Exception e) {
            log.error("Error processing baggage-created event: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "baggage-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBaggageUpdated(BaggageEventDto event) {
        log.info("Received baggage-updated event: baggageId={}, status={}", 
                event.getBaggageId(), event.getStatus());
        
        try {
            cacheService.invalidate("baggage:" + event.getBaggageId());
            cacheService.invalidate("baggage:barcode:" + event.getBarcode());
            cacheService.invalidatePattern("baggage:passenger:" + event.getPassengerId() + "*");
            cacheService.invalidatePattern("baggage:flight:" + event.getFlightNumber() + "*");
            
            log.info("Successfully processed baggage-updated event");
        } catch (Exception e) {
            log.error("Error processing baggage-updated event: {}", e.getMessage(), e);
        }
    }
}
