package com.matrix.ecommerce.dtos.dto.dto.card;

public class CardDTO {
    private Long id;
    private String maskedCardNumber;  // e.g., **** **** **** 1234
    private int expiryMonth;
    private int expiryYear;
    private String cardType;
}
