package com.matrix.ecommerce.dtos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponseDTO {
    private Long userId;
    private String message; // "Registration Successful"
}
