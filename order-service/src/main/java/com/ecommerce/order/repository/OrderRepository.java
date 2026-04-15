package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByUserId(String userId, Pageable pageable);
    Optional<Order> findByIdAndUserId(String id, String userId);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);
}
