package com.matrix.ecommerce.dtos.dto.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDTO {
    private String token;   // JWT token
    private Long userId;
    private String role;
}
