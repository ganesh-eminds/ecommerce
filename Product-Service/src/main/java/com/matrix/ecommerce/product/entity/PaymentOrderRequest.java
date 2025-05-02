package com.matrix.ecommerce.product.entity;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PaymentOrderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;
    private double amount;
    private String paymentStatus;
    private PaymentMethod paymentMethod;
}
