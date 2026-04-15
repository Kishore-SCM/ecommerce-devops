package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record UpdateProductRequest(
    String name,
    String description,
    @DecimalMin("0.01") BigDecimal price,
    @Min(0) Integer stock,
    Boolean featured,
    Product.Category category
) {}
