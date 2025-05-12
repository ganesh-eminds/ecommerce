package com.matrix.ecommerce.user.service;

import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationRequest;
import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationResponse;
import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.entity.coupon.Coupon;
import com.matrix.ecommerce.user.entity.coupon.CouponUsage;
import com.matrix.ecommerce.user.repository.CouponRepository;
import com.matrix.ecommerce.user.repository.CouponUsageRepository;
import com.matrix.ecommerce.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CouponService {

    @Autowired private CouponRepository couponRepository;
    @Autowired private CouponUsageRepository couponUsageRepository;
    @Autowired private UserRepository userRepository;

    public CouponValidationResponse applyCoupon(CouponValidationRequest request) {
        log.info("Applying coupon: {} for user: {}", request.getCouponCode(), request.getUserId());
        boolean isValid = true;
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

        if (!coupon.isActive() || coupon.getExpiryDate().isBefore(LocalDate.now())) {
            isValid = false;
            throw new IllegalStateException("Coupon expired or inactive");
        }

        if (coupon.getAllowedUserIds() != null && !coupon.getAllowedUserIds().isEmpty()
                && !coupon.getAllowedUserIds().contains(request.getUserId())) {
            isValid = false;
            throw new IllegalStateException("Coupon not valid for this user");
        }

        if (coupon.getMinimumPurchaseAmount() != null && request.getTotalAmount().doubleValue() < coupon.getMinimumPurchaseAmount()) {
            isValid = false;
            throw new IllegalStateException("Order amount below coupon minimum");
        }

        if (coupon.getMaxGlobalUsages() != null &&
                couponUsageRepository.countByCoupon(coupon) >= coupon.getMaxGlobalUsages()) {
            isValid = false;
            throw new IllegalStateException("Coupon usage limit reached");
        }

        if (coupon.getMaxUsagesPerUser() != null &&
                couponUsageRepository.countByCouponAndUser(coupon, getUserById(request.getUserId())) >= coupon.getMaxUsagesPerUser()) {
            isValid = false;
            throw new IllegalStateException("Coupon usage limit per user reached");
        }

        double discount = 0;
        if (coupon.getDiscountAmount() != null) {
            discount = coupon.getDiscountAmount();
        } else if (coupon.getDiscountPercentage() != null) {
            discount = (coupon.getDiscountPercentage() / 100.0) * request.getTotalAmount().doubleValue();
        }

        // Save usage
        CouponUsage usage = new CouponUsage();
        usage.setCoupon(coupon);
        usage.setUser(getUserById(request.getUserId()));
        usage.setUsedAt(LocalDate.now());
        usage.setDiscountApplied(discount);
        couponUsageRepository.save(usage);

        log.info("Coupon applied successfully: {} with discount: {}", coupon.getCode(), discount);
        CouponValidationResponse response = new CouponValidationResponse();
        response.setMessage("Coupon applied successfully");
        response.setDiscountedAmount(new BigDecimal(discount));
        response.setValid(isValid);
        return response;
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }

}
