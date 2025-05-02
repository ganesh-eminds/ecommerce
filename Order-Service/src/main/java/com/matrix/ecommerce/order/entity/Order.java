package com.matrix.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID productId;
    private int quantity;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
