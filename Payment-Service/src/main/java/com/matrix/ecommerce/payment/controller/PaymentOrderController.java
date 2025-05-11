package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.dtos.dto.dto.payment.PayOrderRequest;
import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.service.PaymentOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
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
        paymentOrderService.createPayment(payOrderRequest.getOrderId());
        return ResponseEntity.ok("Amount is paid for the order "+ payOrderRequest.getOrderId() );
    }
    // cancel payment by order id
    @PutMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody UUID orderId) {
        log.info("Cancelling payment for order ID: {}", orderId);
        paymentOrderService.cancelPayment(orderId);
        return ResponseEntity.ok("Cancelled the order "+ orderId);
    }
    @PostMapping("/payments/by-ids")
    public List<PayOrderRequest> getPaymentsByIds(@RequestBody Set<UUID> ids) {
        return paymentOrderService.getPaymentsByIds(ids); // convert entity → DTO
    }

}
