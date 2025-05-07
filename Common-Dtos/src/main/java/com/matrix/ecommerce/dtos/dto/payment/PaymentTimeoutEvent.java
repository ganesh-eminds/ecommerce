package com.matrix.ecommerce.dtos.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTimeoutEvent {
    private UUID orderId;
    private List<UUID> productIds;
}
