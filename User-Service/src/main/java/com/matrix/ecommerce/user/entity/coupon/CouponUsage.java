package com.matrix.ecommerce.user.entity.coupon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matrix.ecommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @JsonManagedReference
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    @ToString.Exclude
    private User user;

    private LocalDate usedAt;

    private Double discountApplied;

    @Override
    public String toString() {
        return "CouponUsage{" +
                "id=" + id +
                ", coupon=" + coupon +
                ", usedAt=" + usedAt +
                ", discountApplied=" + discountApplied +
                '}';
    }
}
