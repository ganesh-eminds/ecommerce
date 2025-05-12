package com.matrix.ecommerce.user.service;

import com.matrix.ecommerce.dtos.dto.dto.coupon.UserCouponDto;
import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.entity.coupon.Coupon;
import com.matrix.ecommerce.user.entity.coupon.CouponUsage;
import com.matrix.ecommerce.user.repository.CouponRepository;
import com.matrix.ecommerce.user.repository.CouponUsageRepository;
import com.matrix.ecommerce.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final UserRepository userRepository;

    @Transactional
    public BigDecimal applyCoupon(UserCouponDto userCouponDto) {
        Coupon coupon = couponRepository.findByCode(userCouponDto.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid coupon"));

        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) throw new RuntimeException("Expired coupon");
        if (coupon.getMinOrderValue() != null && userCouponDto.getOrderTotal().compareTo(coupon.getMinOrderValue()) < 0)
            throw new RuntimeException("Order below minimum");

        if (coupon.isFirstOrderOnly() && !userCouponDto.isFirstOrder())
            throw new RuntimeException("Only for first order");

        if (coupon.getUsageLimit() > 0 && coupon.getTotalUses() >= coupon.getUsageLimit())
            throw new RuntimeException("Usage limit reached");

        if (couponUsageRepository.existsByUserIdAndCoupon_Code(userCouponDto.getUserId(), userCouponDto.getCode()))
            throw new RuntimeException("Already used this coupon");

        BigDecimal discount = coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() :
                userCouponDto.getOrderTotal().multiply(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100));

        if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0)
            discount = coupon.getMaxDiscount();

        BigDecimal finalAmount = userCouponDto.getOrderTotal().subtract(discount);


        // Audit Log
        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .user(userRepository.findById(userCouponDto.getUserId()).orElse(new User()))
                .orderId(userCouponDto.getOrderId())
                .orderAmount(userCouponDto.getOrderTotal())
                .discountApplied(discount)
                .usedAt(LocalDateTime.now())
                .build();

        couponUsageRepository.save(usage);
        coupon.setTotalUses(coupon.getTotalUses() + 1);
        couponRepository.save(coupon);

        return finalAmount;
    }

}
