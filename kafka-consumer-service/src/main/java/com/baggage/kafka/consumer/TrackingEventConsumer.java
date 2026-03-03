package com.baggage.kafka.consumer;

import com.baggage.dto.event.TrackingEventDto;
import com.baggage.kafka.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TrackingEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(TrackingEventConsumer.class);
    
    @Autowired
    private CacheService cacheService;
    
    @KafkaListener(topics = "tracking-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTrackingUpdated(TrackingEventDto event) {
        log.info("Received tracking-updated event: trackingId={}, baggageId={}, status={}", 
                event.getTrackingId(), event.getBaggageId(), event.getStatus());
        
        try {
            // Invalidate tracking history cache
            cacheService.invalidatePattern("tracking:baggage:" + event.getBaggageId() + "*");
            
            // Cache latest tracking
            String latestKey = "tracking:latest:" + event.getBaggageId();
            cacheService.cache(latestKey, event, 1, TimeUnit.HOURS);
            
            log.info("Successfully processed tracking-updated event");
        } catch (Exception e) {
            log.error("Error processing tracking-updated event: {}", e.getMessage(), e);
        }
    }
}
