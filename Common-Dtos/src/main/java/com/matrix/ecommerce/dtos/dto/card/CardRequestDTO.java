package com.matrix.ecommerce.dtos.dto.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestDTO {
    @NotBlank
    private String cardNumber;   // masked or encrypted
    @Min(1)
    @Max(12)
    private int expiryMonth;
    private int expiryYear;
    private String cardType;     // VISA/MASTERCARD
}
