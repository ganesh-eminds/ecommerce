package com.matrix.ecommerce.payment.entity;

//import com.matrix.ecommerce.user.entity.User;
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
    private String paymentStatus;
    private String paymentMethod;
}
