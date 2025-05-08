package com.matrix.ecommerce.order.repository;

import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime cutoff);
}
