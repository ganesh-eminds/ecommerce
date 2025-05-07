package com.matrix.ecommerce.payment.service;

import com.matrix.ecommerce.dtos.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.exception.ValidationException;
import com.matrix.ecommerce.payment.event.PaymentOrderRequest;
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
                .orElseThrow(() -> new ValidationException(new ExceptionDto("PAYMENT_NOT_FOUND_WITH_ORDER_ID","404", "BAD REQUEST", "Payment Not Found with Order Id: " + orderId)));
    }
    public List<PaymentOrderRequest> getAllOrderPayments() {
        return paymentOrderRepository.findAll();
    }
    public void createPayment(UUID orderId) {
        log.info("Creating payment for order ID: {}", orderId);
        PaymentOrderRequest paymentOrderRequest = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException(new ExceptionDto("PAYMENT_NOT_FOUND_WITH_ORDER_ID","404", "BAD REQUEST", "Payment Not Found with Order Id: " + orderId)));
        paymentEventListener.handlePaymentInitiated(paymentOrderRequest);
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
                .orElseThrow(() -> new ValidationException(new ExceptionDto("PAYMENT_NOT_FOUND_WITH_PAYMENT_ID","404", "BAD REQUEST", "Payment Not Found with Payment Id: " + paymentId)));

        paymentOrderRepository.delete(existingPayment);
    }

}
