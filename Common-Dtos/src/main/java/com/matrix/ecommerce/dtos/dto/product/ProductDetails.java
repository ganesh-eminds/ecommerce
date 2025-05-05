package com.matrix.ecommerce.dtos.dto.product;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
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
    private Integer quantity;
    private Double price;
    private PaymentMethod paymentMethod;
}
