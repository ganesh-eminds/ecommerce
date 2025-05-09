package com.matrix.ecommerce.dtos.dto.dto.response;

import com.matrix.ecommerce.dtos.dto.dto.address.AddressDTO;
import com.matrix.ecommerce.dtos.dto.dto.card.CardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<AddressDTO> addresses;
    private List<CardDTO> cards;
}
