package com.matrix.ecommerce.dtos.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAddressRequest {

    @Size(max = 100)
    private String street;

    @Size(max = 50)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 10)
    private String postalCode;

    @Size(max = 50)
    private String country;
}
