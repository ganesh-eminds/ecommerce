package com.matrix.ecommerce.payment.entity;

//import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.payment.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PaymentOrderRequest {
    @Id
    private UUID orderId;
    private double amount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
}
