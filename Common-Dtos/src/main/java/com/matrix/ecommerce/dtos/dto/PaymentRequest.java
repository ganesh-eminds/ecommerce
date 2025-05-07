package com.matrix.ecommerce.dtos.dto;

//import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    @Id
    private UUID orderId;
    private double amount;
    private String paymentStatus;
    private PaymentMethod paymentMethod;
}
