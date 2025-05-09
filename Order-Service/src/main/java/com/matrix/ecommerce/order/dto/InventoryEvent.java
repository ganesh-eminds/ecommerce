package com.matrix.ecommerce.order.dto;

import com.matrix.ecommerce.dtos.dto.dto.product.ProductRequest;
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
public class InventoryEvent {
    private UUID orderId;
    private List<ProductRequest> products;
}
