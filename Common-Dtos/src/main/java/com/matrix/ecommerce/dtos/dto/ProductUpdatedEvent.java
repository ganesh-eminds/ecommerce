package com.matrix.ecommerce.dtos.dto;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdatedEvent {
    private UUID orderId;
    private double totalPrice;
    private int quantity;
    private PaymentMethod paymentMethod;
}
