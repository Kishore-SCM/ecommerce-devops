package com.ecommerce.product;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepo;
    @Mock software.amazon.awssdk.services.s3.S3Client s3Client;

    ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(productRepo, s3Client, new SimpleMeterRegistry());
    }

    @Test
    void getById_existingProduct_returnsResponse() {
        Product p = Product.builder().id("uuid-123").name("Test").price(BigDecimal.valueOf(29.99))
            .stock(50).sku("TST-001").category(Product.Category.ELECTRONICS)
            .active(true).rating(4.5).reviewCount(100).featured(false).build();
        when(productRepo.findById("uuid-123")).thenReturn(Optional.of(p));
        ProductResponse result = service.getById("uuid-123");
        assertThat(result.id()).isEqualTo("uuid-123");
        assertThat(result.price()).isEqualByComparingTo("29.99");
    }

    @Test
    void getById_unknownId_throwsNotFoundException() {
        when(productRepo.findById("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById("bad"))
            .isInstanceOf(ProductNotFoundException.class);
    }
}
