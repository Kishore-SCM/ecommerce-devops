package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
          AND (:category IS NULL OR p.category = :category)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:search IS NULL
               OR LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%',:search,'%')))
        ORDER BY p.createdAt DESC
        """)
    Page<Product> searchProducts(
        @Param("category") Product.Category category,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("search")   String search,
        Pageable pageable);

    List<Product> findByFeaturedTrueAndActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.stock < :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}
