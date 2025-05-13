package com.matrix.ecommerce.dtos.dto.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matrix.ecommerce.dtos.dto.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.dto.product.ProductRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {

    @NotNull
    private List<ProductRequest> products;

    @NotNull
    private UUID userId;

    @NotNull
    private PaymentMethod paymentMethod;

    private String coupon;

    private ExceptionDto exception;

    public OrderRequest(ExceptionDto exception) {
        this.exception = exception;
    }
}
