package com.matrix.ecommerce.dtos.dto.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponValidationResponse {
    private boolean valid;
    private BigDecimal discountedAmount;
    private String message;
}
