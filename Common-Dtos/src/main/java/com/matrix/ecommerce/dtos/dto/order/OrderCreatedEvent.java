package com.matrix.ecommerce.dtos.dto.order;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent {
    private UUID orderId;
    private List<ProductDetails> productDetails;
    private PaymentMethod paymentMethod;
    private String eventType;
}
