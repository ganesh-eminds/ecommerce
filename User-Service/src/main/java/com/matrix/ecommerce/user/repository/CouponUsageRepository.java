package com.matrix.ecommerce.user.repository;

import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.entity.coupon.Coupon;
import com.matrix.ecommerce.user.entity.coupon.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
//    int countByCoupon(Coupon coupon);
//    int countByCouponAndUser(Coupon coupon, User user);

    // CouponUsageRepository
    long countByCouponId(UUID couponId);

    long countByCouponIdAndUserId(UUID couponId, UUID userId);

}