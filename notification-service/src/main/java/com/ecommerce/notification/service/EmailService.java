package com.ecommerce.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service @RequiredArgsConstructor @Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@shopwave.com}")
    private String fromAddress;

    public void sendOrderConfirmation(String toEmail, String orderId, BigDecimal total) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Order Confirmation — #" + orderId);
            helper.setText(buildHtml(orderId, total), true);
            mailSender.send(message);
            log.info("Confirmation sent to {} for order {}", toEmail, orderId);
        } catch (Exception e) {
            log.error("Email send failed: {}", e.getMessage());
            throw new RuntimeException("Email send failed", e);
        }
    }

    private String buildHtml(String orderId, BigDecimal total) {
        return """
            <html><body style="font-family:Arial,sans-serif">
              <div style="background:#4F46E5;padding:20px;text-align:center">
                <h1 style="color:white">ShopWave</h1>
              </div>
              <div style="padding:30px">
                <h2>Thank you for your order!</h2>
                <p>Order <strong>#%s</strong> confirmed.</p>
                <p>Total: <strong>$%s</strong></p>
              </div>
            </body></html>""".formatted(orderId, total.toPlainString());
    }
}
