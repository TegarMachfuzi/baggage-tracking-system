package com.baggage.notification.consumer;

import com.baggage.dto.event.BaggageEventDto;
import com.baggage.notification.service.EmailService;
import com.baggage.notification.service.NotificationTemplateService;
import com.baggage.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BaggageEventConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(BaggageEventConsumer.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private NotificationTemplateService templateService;
    
    @KafkaListener(topics = "baggage-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBaggageCreated(BaggageEventDto event) {
        log.info("Received baggage-created event: baggageId={}, barcode={}", 
                event.getBaggageId(), event.getBarcode());
        
        try {
            // TODO: Get passenger email/phone from passenger-service
            String passengerEmail = "passenger@example.com"; // Mock
            String passengerPhone = "+1234567890"; // Mock
            
            // Send email notification
            String subject = templateService.getBaggageCreatedEmailSubject();
            String body = templateService.getBaggageCreatedEmailBody(
                event.getBarcode(), 
                event.getFlightNumber(), 
                event.getDestination()
            );
            emailService.sendEmail(passengerEmail, subject, body);
            
            // Send SMS notification
            String smsMessage = templateService.getBaggageCreatedSms(
                event.getBarcode(), 
                event.getFlightNumber()
            );
            smsService.sendSms(passengerPhone, smsMessage);
            
            log.info("Notifications sent for baggage-created: {}", event.getBarcode());
        } catch (Exception e) {
            log.error("Error processing baggage-created event: {}", e.getMessage(), e);
        }
    }
}
