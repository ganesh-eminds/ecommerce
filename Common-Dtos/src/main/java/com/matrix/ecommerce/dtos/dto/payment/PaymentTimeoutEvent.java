package com.matrix.ecommerce.dtos.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTimeoutEvent {
    private UUID orderId;
    private UUID productId;
}
