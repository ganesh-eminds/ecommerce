package com.matrix.ecommerce.dtos.dto.dto.card;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CardResponse {

    private UUID id;
    private String cardNumberMasked;
    private String cardHolderName;
    private String expiryDate;
}
