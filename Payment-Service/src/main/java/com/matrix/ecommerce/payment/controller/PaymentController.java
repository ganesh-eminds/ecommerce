package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.entity.Payment;
import com.matrix.ecommerce.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

}
