package com.matrix.ecommerce.product.repository;

import com.matrix.ecommerce.product.entity.PaymentOrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrderRequest, UUID> {
}
