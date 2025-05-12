package com.matrix.ecommerce.user.controller;

import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationRequest;
import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationResponse;
import com.matrix.ecommerce.dtos.dto.dto.coupon.UserCouponDto;
import com.matrix.ecommerce.user.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

//    @PostMapping("/apply")
//    public BigDecimal applyCoupon(@RequestBody UserCouponDto userCouponDto) {
//        return couponService.applyCoupon(userCouponDto);
//    }

    @PostMapping("/apply")
    public ResponseEntity<CouponValidationResponse> validateCoupon(@RequestBody CouponValidationRequest request) {
        return ResponseEntity.ok(couponService.applyCoupon(request));
    }

}
