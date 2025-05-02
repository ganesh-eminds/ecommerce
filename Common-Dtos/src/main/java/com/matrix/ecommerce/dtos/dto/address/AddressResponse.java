package com.matrix.ecommerce.dtos.dto.address;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddressResponse {

    private UUID id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
