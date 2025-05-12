package com.matrix.ecommerce.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matrix.ecommerce.user.entity.coupon.Coupon;
import com.matrix.ecommerce.user.entity.coupon.CouponUsage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String firstName;

    private String username;

    private String email;

    private String password;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private Set<UUID> paymentOrderIds;
    private Set<UUID> orderIds;

    private double balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CouponUsage> usages;

}
