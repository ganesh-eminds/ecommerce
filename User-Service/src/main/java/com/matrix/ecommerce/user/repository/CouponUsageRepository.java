package com.matrix.ecommerce.user.repository;

import com.matrix.ecommerce.user.entity.coupon.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {
    boolean existsByUserIdAndCoupon_Code(UUID userId, String code);
}
