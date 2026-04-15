package com.ecommerce.notification.listener;

import com.ecommerce.notification.event.OrderPlacedEvent;
import com.ecommerce.notification.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class OrderEventListener {

    private final EmailService emailService;

    @SqsListener("${aws.sqs.order-events-queue}")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order event: orderId={}", event.orderId());
        emailService.sendOrderConfirmation(event.userEmail(), event.orderId(), event.totalAmount());
    }
}
