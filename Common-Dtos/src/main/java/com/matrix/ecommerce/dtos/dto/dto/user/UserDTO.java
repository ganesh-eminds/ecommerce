package com.matrix.ecommerce.dtos.dto.dto.user;

import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.payment.PayOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String firstName;
    private String username;
    private String email;
    private Set<String> roles;

    private Set<OrderRequest> orders;     // 👈 User's orders from Order Service
    private Set<PayOrderRequest> payments; // 👈 User's payments from Payment Service
}
