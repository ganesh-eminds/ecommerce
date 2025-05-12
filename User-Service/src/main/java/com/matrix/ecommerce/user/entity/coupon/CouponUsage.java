package com.matrix.ecommerce.user.entity.coupon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matrix.ecommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    private UUID orderId;

    private BigDecimal orderAmount;

    private BigDecimal discountApplied;

    private LocalDateTime usedAt;
}
