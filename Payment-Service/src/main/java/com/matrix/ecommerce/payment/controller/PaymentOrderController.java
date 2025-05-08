package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.service.PaymentOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Retrieve all payment orders in the system.
     * @return A list of all payment order requests.
     */
    @Operation(summary = "Retrieve all payment orders", description = "Fetches all payment orders created in the system.")
    @GetMapping("/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payment orders retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentOrderRequest>> getAllPayments() {
        return ResponseEntity.ok(paymentOrderService.getAllOrderPayments());
    }

    /**
     * Retrieve a specific payment order by its associated order ID.
     * @param orderId The unique identifier of the order.
     * @return Payment order details for the provided order ID.
     */
    @Operation(summary = "Retrieve payment order by order ID", description = "Fetches the payment order details for a specific order using the order ID.")
    @GetMapping("/{orderId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment order not found for the provided order ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentOrderRequest> getPaymentById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentOrderService.getPaymentByOrderId(orderId));
    }

    /**
     * Create a new payment for a given order ID.
     * @param orderId The unique identifier of the order.
     * @return Response message indicating the success of the payment creation.
     */
    @Operation(summary = "Create a payment for an order", description = "Creates a new payment record for the given order ID.")
    @PostMapping("/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid order ID provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> createPayment(@RequestBody UUID orderId) {
        log.info("Creating payment for order ID: {}", orderId);
        paymentOrderService.createPayment(orderId);
        return ResponseEntity.ok("Payment created successfully");
    }
}
