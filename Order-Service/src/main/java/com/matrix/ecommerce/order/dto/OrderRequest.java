package com.matrix.ecommerce.order.dto;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull
    private List<ProductRequest> products;

    @NotNull
    private PaymentMethod paymentMethod;
}
