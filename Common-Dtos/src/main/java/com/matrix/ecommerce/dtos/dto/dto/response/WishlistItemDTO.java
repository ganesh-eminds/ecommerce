package com.matrix.ecommerce.dtos.dto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistItemDTO {
    private Long productId;
    private String productName;
    private String imageUrl;
    private double price;
}
