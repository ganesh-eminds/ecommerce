package com.matrix.ecommerce.order.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productId;
    private int quantity;
    private String paymentMethod;

}
