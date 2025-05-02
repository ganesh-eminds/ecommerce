package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

@GetMapping("/create")
public ResponseEntity<String> createPayment(@PathVariable UUID orderId) {
    paymentOrderService.createPayment(orderId);
    return ResponseEntity.ok("Payment created successfully");
}
}
