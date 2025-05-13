package com.matrix.ecommerce.user.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderStatus;
import com.matrix.ecommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_order")
public class UserOrder {
    @Id
    private UUID orderId;
    @ManyToOne
    @JoinColumn(name = "user_order_id")
    @ToString.Exclude
    @JsonBackReference
    private User user;
    @Enumerated(EnumType.STRING)
    @JsonManagedReference
    private OrderStatus orderStatus;

    @Override
    public String toString() {
        return "UserOrder{" +
                "orderId=" + orderId +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
