package com.matrix.ecommerce.user.client;

import com.matrix.ecommerce.dtos.dto.dto.payment.PayOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {
    @PostMapping("/api/payment-orders/payments/by-ids")
    List<PayOrderRequest> getPaymentsByIds(@RequestBody Set<UUID> paymentOrderIds);
}
