package com.matrix.ecommerce.dtos.dto.dto.order;

import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.dto.product.ProductRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull
    private List<ProductRequest> products;

    @NotNull
    private UUID userId;

    @NotNull
    private PaymentMethod paymentMethod;

}
