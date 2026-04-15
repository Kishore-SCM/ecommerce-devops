package com.ecommerce.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_sku",      columnList = "sku", unique = true)
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(unique = true, nullable = false)
    private String sku;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ElementCollection
    @CollectionTable(name = "product_images")
    private List<String> imageUrls;

    private Double  rating;
    private Integer reviewCount;
    private Boolean featured;
    private Boolean active;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp  private LocalDateTime updatedAt;

    public enum Category {
        ELECTRONICS, CLOTHING, BOOKS, HOME, SPORTS, BEAUTY, FOOD, TOYS
    }
}
