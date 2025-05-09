package com.matrix.ecommerce.payment.service;

import com.matrix.ecommerce.dtos.dto.payment.PaymentStatus;
import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.event.PaymentEventListener;
import com.matrix.ecommerce.payment.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentOrderService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventListener paymentEventListener;

    public PaymentOrderRequest getPaymentByOrderId(UUID orderId) {
        return paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with order id: " + orderId));
    }

    public List<PaymentOrderRequest> getAllOrderPayments() {
        return paymentOrderRepository.findAll();
    }

    public void createPayment(UUID orderId) {
        log.info("Creating payment for order ID: {}", orderId);
        PaymentOrderRequest paymentOrderRequest = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with order id: " + orderId));
        paymentEventListener.handlePaymentInitiated(paymentOrderRequest, true);
        paymentOrderRequest.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentOrderRepository.save(paymentOrderRequest);
    }

    public PaymentOrderRequest updatePayment(UUID paymentId, PaymentOrderRequest paymentRequest) {
        PaymentOrderRequest existingPayment = paymentOrderRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        existingPayment.setAmount(paymentRequest.getAmount());
        existingPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        existingPayment.setPaymentStatus(paymentRequest.getPaymentStatus());

        return paymentOrderRepository.save(existingPayment);
    }
    public void deletePayment(UUID paymentId) {
        PaymentOrderRequest existingPayment = paymentOrderRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        paymentOrderRepository.delete(existingPayment);
    }

    public void cancelPayment(UUID orderId) {
        PaymentOrderRequest paymentOrderRequest = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with order id: " + orderId));
        paymentOrderRequest.setPaymentStatus(PaymentStatus.CANCELLED);
        paymentOrderRepository.save(paymentOrderRequest);
        log.info("Payment cancelled for order ID: {}", orderId);
        // Notify payment failed and restore product stock
        paymentEventListener.handlePaymentInitiated(paymentOrderRequest, false);

    }
}
