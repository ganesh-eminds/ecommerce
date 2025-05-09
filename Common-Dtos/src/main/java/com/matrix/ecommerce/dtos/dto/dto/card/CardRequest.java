package com.matrix.ecommerce.dtos.dto.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CardRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank
    @Size(max = 50)
    private String cardHolderName;

    @NotBlank
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiry must be in MM/YY format")
    private String expiryDate;

    @NotBlank
    @Pattern(regexp = "\\d{3,4}", message = "Invalid CVV")
    private String cvv;
}
