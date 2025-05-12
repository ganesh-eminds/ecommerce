package com.matrix.ecommerce.dtos.dto.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCouponDto {
    UUID userId;
    UUID orderId;
    String code;
    BigDecimal orderTotal;
    boolean firstOrder;
}
