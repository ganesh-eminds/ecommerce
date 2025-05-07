package com.matrix.ecommerce.dtos.dto.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payments")
public class PaymentDto {

    @Id
    private UUID id;
    private UUID orderId;
    private Double amount;
    private String paymentStatus;
    private String paymentMethod;
}
