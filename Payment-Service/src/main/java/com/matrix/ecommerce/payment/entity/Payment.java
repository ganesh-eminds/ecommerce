package com.matrix.ecommerce.payment.entity;

import com.matrix.ecommerce.dtos.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.payment.PaymentStatus;
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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
