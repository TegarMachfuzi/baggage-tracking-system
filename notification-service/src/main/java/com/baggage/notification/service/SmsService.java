package com.baggage.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    
    @Value("${notification.sms.enabled}")
    private boolean enabled;
    
    public void sendSms(String phoneNumber, String message) {
        if (!enabled) {
            log.info("SMS disabled. Would send to: {}, message: {}", phoneNumber, message);
            return;
        }
        
        // TODO: Integrate with SMS provider (Twilio, AWS SNS, etc.)
        log.info("SMS sent to {}: {}", phoneNumber, message);
    }
}
