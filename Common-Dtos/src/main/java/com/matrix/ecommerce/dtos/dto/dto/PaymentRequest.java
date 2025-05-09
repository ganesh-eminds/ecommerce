package com.matrix.ecommerce.dtos.dto.dto;

//import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentStatus;
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
    private UUID orderId;
    private double amount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
}
