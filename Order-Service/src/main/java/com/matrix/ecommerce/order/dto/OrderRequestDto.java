package com.matrix.ecommerce.order.dto;

import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;
    private int quantity;
    private PaymentMethod paymentMethod;

}
