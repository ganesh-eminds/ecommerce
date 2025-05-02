package com.matrix.ecommerce.dtos.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull
    private OrderStatus status;
}
