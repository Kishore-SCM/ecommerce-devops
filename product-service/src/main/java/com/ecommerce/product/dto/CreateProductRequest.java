package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank @Size(min = 2, max = 200) String name,
    String description,
    @NotNull @DecimalMin("0.01") BigDecimal price,
    @NotNull @Min(0) Integer stock,
    @NotBlank @Size(max = 50) String sku,
    @NotNull Product.Category category,
    Boolean featured
) {}
