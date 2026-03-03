package com.baggage.notification.consumer;

import com.baggage.dto.event.TrackingEventDto;
import com.baggage.notification.service.EmailService;
import com.baggage.notification.service.NotificationTemplateService;
import com.baggage.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TrackingEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(TrackingEventConsumer.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @KafkaListener(topics = "tracking-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTrackingUpdated(TrackingEventDto event) {
        log.info("Received tracking-updated event: baggageId={}, location={}, status={}", 
                event.getBaggageId(), event.getLocation(), event.getStatus());
        
        try {
            // TODO: Get passenger email/phone from baggage-service -> passenger-service
            String passengerEmail = "passenger@example.com"; // Mock
            String passengerPhone = "+1234567890"; // Mock
            String barcode = "MOCK-BARCODE"; // Mock - should get from baggage
            
            // Send email notification
            String subject = templateService.getTrackingUpdatedEmailSubject();
            String body = templateService.getTrackingUpdatedEmailBody(
                barcode,
                event.getLocation(),
                event.getStatus().toString()
            );
            emailService.sendEmail(passengerEmail, subject, body);
            
            // Send SMS notification
            String smsMessage = templateService.getTrackingUpdatedSms(barcode, event.getLocation());
            smsService.sendSms(passengerPhone, smsMessage);
            
            log.info("Notifications sent for tracking-updated: baggageId={}", event.getBaggageId());
        } catch (Exception e) {
            log.error("Error processing tracking-updated event: {}", e.getMessage(), e);
        }
    }
}
