package com.matrix.ecommerce.user.entity.coupon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    private BigDecimal discountAmount;      // for flat discount
    private BigDecimal discountPercent;     // for percentage discount
    private BigDecimal maxDiscount;         // max discount limit
    private BigDecimal minOrderValue;       // min value to apply

    private boolean firstOrderOnly;

    private LocalDateTime expiryDate;

    private int usageLimit;

    private int totalUses;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CouponUsage> usages;

}
