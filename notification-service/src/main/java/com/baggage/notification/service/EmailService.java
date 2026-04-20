package com.baggage.notification.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired private JavaMailSender mailSender;
    @Autowired private BarcodeGeneratorService barcodeGeneratorService;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.enabled}")
    private boolean enabled;

    public void sendEmail(String to, String subject, String htmlBody) {
        sendEmail(to, subject, htmlBody, null);
    }

    public void sendEmail(String to, String subject, String htmlBody, String barcode) {
        if (!enabled) {
            log.info("Email disabled. Would send to: {}, subject: {}", to, subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            if (barcode != null) {
                String html = htmlBody.replace("{{BARCODE_IMAGE}}",
                    "<img src='cid:barcode' alt='" + barcode + "' style='display:block;margin:16px 0;'/>");
                helper.setText(html, true);
                byte[] barcodeImage = barcodeGeneratorService.generateBarcode(barcode);
                helper.addInline("barcode", new org.springframework.core.io.ByteArrayResource(barcodeImage), "image/png");
            } else {
                helper.setText(htmlBody, true);
            }

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
