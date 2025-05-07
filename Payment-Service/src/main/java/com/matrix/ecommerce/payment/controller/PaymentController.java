package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.entity.Payment;
import com.matrix.ecommerce.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @DeleteMapping
    public ResponseEntity<Payment> getPaymentById(@RequestBody UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

}
