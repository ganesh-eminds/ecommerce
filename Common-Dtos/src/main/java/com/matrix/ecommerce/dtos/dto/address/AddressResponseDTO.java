package com.matrix.ecommerce.dtos.dto.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {
    private Long addressId;
    private String message; // "Address added successfully"
}
