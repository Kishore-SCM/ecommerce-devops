package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
    String id, String name, String description,
    BigDecimal price, Integer stock, String sku,
    Product.Category category, List<String> imageUrls,
    Double rating, Integer reviewCount,
    Boolean featured, LocalDateTime createdAt
) {}
