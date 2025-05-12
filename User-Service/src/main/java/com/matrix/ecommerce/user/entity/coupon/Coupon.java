package com.matrix.ecommerce.user.entity.coupon;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private UUID id;

    private String code;

    private String description;

    private Double discountAmount; // For flat discount

    private Double discountPercentage; // For percentage discount

    private boolean firstOrderOnly;

    private Double minimumPurchaseAmount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coupon_allowed_users", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "user_id")
    private List<UUID> allowedUserIds;

    private LocalDate expiryDate;

    private Integer maxGlobalUsages;

    private Integer maxUsagesPerUser;

    private boolean active;
}
