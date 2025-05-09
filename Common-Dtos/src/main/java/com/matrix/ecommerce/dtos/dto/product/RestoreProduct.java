package com.matrix.ecommerce.dtos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreProduct {

    private UUID orderId;
    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String imagePath;
}

