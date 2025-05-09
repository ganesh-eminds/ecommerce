package com.matrix.ecommerce.dtos.dto.dto.product;

import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {

    private UUID productId;
    private UUID userId;
    private Integer quantity;
    private Double price;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
