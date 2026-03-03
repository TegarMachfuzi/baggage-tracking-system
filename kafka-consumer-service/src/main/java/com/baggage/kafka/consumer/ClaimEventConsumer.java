package com.baggage.kafka.consumer;

import com.baggage.dto.event.ClaimEventDto;
import com.baggage.kafka.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ClaimEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(ClaimEventConsumer.class);
    
    @Autowired
    private CacheService cacheService;
    
    @KafkaListener(topics = "claim-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClaimCreated(ClaimEventDto event) {
        log.info("Received claim-created event: claimId={}, baggageId={}, type={}", 
                event.getClaimId(), event.getBaggageId(), event.getClaimType());
        
        try {
            // Cache claim
            String cacheKey = "claim:" + event.getClaimId();
            cacheService.cache(cacheKey, event, 1, TimeUnit.HOURS);
            
            // Invalidate baggage cache (status might change)
            cacheService.invalidatePattern("baggage:" + event.getBaggageId() + "*");
            
            log.info("Successfully processed claim-created event");
        } catch (Exception e) {
            log.error("Error processing claim-created event: {}", e.getMessage(), e);
        }
    }
}
