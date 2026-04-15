package com.ecommerce.order.event;

import java.math.BigDecimal;

public record OrderPlacedEvent(
    String orderId, String userId, String userEmail,
    BigDecimal totalAmount, String shippingAddress
) {}
