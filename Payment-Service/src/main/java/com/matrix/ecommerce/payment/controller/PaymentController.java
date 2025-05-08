package com.matrix.ecommerce.payment.controller;

import com.matrix.ecommerce.payment.entity.Payment;
import com.matrix.ecommerce.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Retrieve all payments in the system.
     * @return A list of all payments.
     */
    @Operation(summary = "Retrieve all payments", description = "Fetches all payments processed in the system.")
    @GetMapping("/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payments retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * Retrieve a specific payment by its ID.
     * @param paymentId The unique identifier of the payment.
     * @return Payment details for the provided payment ID.
     */
    @Operation(summary = "Retrieve payment by ID", description = "Fetches payment details for a specific payment using the payment ID.")
    @GetMapping("/{paymentId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found for the provided payment ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Payment> getPaymentById(UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
}
