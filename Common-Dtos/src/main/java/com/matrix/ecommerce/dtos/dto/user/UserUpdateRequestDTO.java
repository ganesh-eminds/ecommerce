package com.matrix.ecommerce.dtos.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String phone;
}
