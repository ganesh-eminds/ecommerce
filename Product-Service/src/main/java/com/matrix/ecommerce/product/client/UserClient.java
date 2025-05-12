package com.matrix.ecommerce.product.client;

import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationRequest;
import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationResponse;
import com.matrix.ecommerce.dtos.dto.dto.coupon.UserCouponDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
    @PostMapping("/api/coupons/apply")
    CouponValidationResponse getCouponsByUserId(@RequestBody CouponValidationRequest couponValidationRequest);
}
