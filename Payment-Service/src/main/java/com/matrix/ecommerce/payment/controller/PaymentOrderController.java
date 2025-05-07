package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.event.PaymentOrderRequest;
import com.matrix.ecommerce.payment.service.PaymentOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payment-orders")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentOrderController {

    @Autowired
    private PaymentOrderService paymentOrderService;

    @GetMapping("/all")
    public ResponseEntity<List<PaymentOrderRequest>> getAllPayments() {
        return ResponseEntity.ok(paymentOrderService.getAllOrderPayments());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentOrderRequest> getPaymentById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentOrderService.getPaymentByOrderId(orderId));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody UUID orderId) {
        log.info("Creating payment for order ID: {}", orderId);
        paymentOrderService.createPayment(orderId);
        return ResponseEntity.ok("Payment created successfully");
    }
}
