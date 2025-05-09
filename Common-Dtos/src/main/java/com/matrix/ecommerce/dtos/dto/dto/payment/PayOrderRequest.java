package com.matrix.ecommerce.dtos.dto.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayOrderRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID userId;

    @NotNull
    private PaymentStatus paymentStatus;
}
