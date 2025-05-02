package com.matrix.ecommerce.dtos.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDTO {
    @NotBlank
    private String oldPassword;
    
    @NotBlank
    private String newPassword;
}
