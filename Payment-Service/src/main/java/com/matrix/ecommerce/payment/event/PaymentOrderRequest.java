package com.matrix.ecommerce.payment.event;

//import com.matrix.ecommerce.user.entity.User;

import com.matrix.ecommerce.dtos.dto.payment.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PaymentOrderRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;
    private UUID orderId;
    private double amount;
    private String paymentStatus;
    private String paymentMethod;
}
