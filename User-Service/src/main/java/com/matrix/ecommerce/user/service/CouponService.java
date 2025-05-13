package com.matrix.ecommerce.user.service;

import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationRequest;
import com.matrix.ecommerce.dtos.dto.dto.coupon.CouponValidationResponse;
import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.entity.coupon.Coupon;
import com.matrix.ecommerce.user.entity.coupon.CouponUsage;
import com.matrix.ecommerce.user.repository.CouponRepository;
import com.matrix.ecommerce.user.repository.CouponUsageRepository;
import com.matrix.ecommerce.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponUsageRepository couponUsageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public CouponValidationResponse applyCoupon(CouponValidationRequest request) {
        CouponValidationResponse response = new CouponValidationResponse();

        Optional<Coupon> couponOpt = couponRepository.findByCode(request.getCouponCode());
        if (couponOpt.isEmpty()) {
            response.setValid(false);
            response.setMessage("Invalid coupon code.");
            return response;
        }

        Coupon coupon = couponOpt.get();

        // Check if coupon is active
        if (!coupon.isActive()) {
            response.setValid(false);
            response.setMessage("Coupon is inactive.");
            return response;
        }

        // Check expiry
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDate.now())) {
            response.setValid(false);
            response.setMessage("Coupon has expired.");
            return response;
        }

        // Check minimum purchase
        if (coupon.getMinimumPurchaseAmount() != null &&
                request.getTotalAmount().compareTo(BigDecimal.valueOf(coupon.getMinimumPurchaseAmount())) < 0) {
            response.setValid(false);
            response.setMessage("Minimum purchase amount not met.");
            return response;
        }

        // Check allowed users
        if (coupon.getAllowedUserIds() != null && !coupon.getAllowedUserIds().isEmpty() &&
                !coupon.getAllowedUserIds().contains(request.getUserId())) {
            response.setValid(false);
            response.setMessage("Coupon not valid for this user.");
            return response;
        }

        // Check max global usage
        long globalUsages = couponUsageRepository.countByCouponId(coupon.getId());
        if (coupon.getMaxGlobalUsages() != null && globalUsages >= coupon.getMaxGlobalUsages()) {
            response.setValid(false);
            response.setMessage("Coupon usage limit exceeded globally.");
            return response;
        }

        // Check user-specific usage
        long userUsages = couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), request.getUserId());
        if (coupon.getMaxUsagesPerUser() != null && userUsages >= coupon.getMaxUsagesPerUser()) {
            response.setValid(false);
            response.setMessage("Coupon usage limit exceeded for this user.");
            return response;
        }

        // Check first order
        if (coupon.isFirstOrderOnly()) {
            boolean hasOrders = userService.hasUserPlacedFirstOrder(request.getUserId());
            if (hasOrders) {
                response.setValid(false);
                response.setMessage("Coupon is valid only for first order.");
                return response;
            }
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountAmount() != null) {
            discount = BigDecimal.valueOf(coupon.getDiscountAmount());
        } else if (coupon.getDiscountPercentage() != null) {
            discount = request.getTotalAmount()
                    .multiply(BigDecimal.valueOf(coupon.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        log.info("Discount calculated: {}", discount);
        BigDecimal discountedAmount = request.getTotalAmount().subtract(discount).max(BigDecimal.ZERO);
        log.info("Discounted amount: {}", discountedAmount);

        // Optionally save usage (if you want to log it immediately)
        CouponUsage usage = new CouponUsage();
        usage.setCoupon(coupon);
        usage.setUser(getUserById(request.getUserId()));
        usage.setUsedAt(LocalDate.now());
        usage.setDiscountApplied(discount.doubleValue());
        couponUsageRepository.save(usage);

        response.setValid(true);
        response.setMessage("Coupon applied successfully.");
        response.setDiscountedAmount(discountedAmount);
        return response;
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }

}
