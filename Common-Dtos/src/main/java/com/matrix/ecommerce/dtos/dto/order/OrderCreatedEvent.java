package com.matrix.ecommerce.dtos.dto.order;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private UUID orderId;
    private UUID productId;
    private int quantity;
    private PaymentMethod paymentMethod;
}
