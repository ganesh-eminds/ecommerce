package com.matrix.ecommerce.dtos.dto.dto.profile;

import com.matrix.ecommerce.dtos.dto.dto.address.AddressResponse;
import com.matrix.ecommerce.dtos.dto.dto.card.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<AddressResponse> addresses;
    private List<CardResponse> cards;
    private boolean active;
    private LocalDateTime createdAt;
}
