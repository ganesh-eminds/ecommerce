package com.matrix.ecommerce.user.repository;

import com.matrix.ecommerce.user.entity.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
}
