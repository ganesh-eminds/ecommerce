package com.matrix.ecommerce.dtos.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceUpdateEvent {
    private UUID orderId;
    private UUID userId;
    private double amount;
    private boolean isSuccess;
}
