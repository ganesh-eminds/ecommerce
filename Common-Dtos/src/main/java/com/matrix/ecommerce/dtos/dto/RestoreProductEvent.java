package com.matrix.ecommerce.dtos.dto;

import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.dtos.dto.product.RestoreProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreProductEvent {
    private UUID orderId;
    private List<RestoreProduct> products;

}
