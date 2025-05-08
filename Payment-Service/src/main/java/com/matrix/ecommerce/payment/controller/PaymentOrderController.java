package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.dto.PayOrderRequest;
import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
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
    public ResponseEntity<String> createPayment(@RequestBody PayOrderRequest payOrderRequest) {
        log.info("Creating payment for order ID: {}", payOrderRequest.getOrderId());
        paymentOrderService.createPayment(UUID.fromString(String.valueOf(payOrderRequest.getOrderId())));
        return ResponseEntity.ok("Payment created successfully");
    }
}
