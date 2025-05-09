package com.matrix.ecommerce.payment.event;

import com.matrix.ecommerce.dtos.dto.dto.PaymentFailedEvent;
import com.matrix.ecommerce.dtos.dto.dto.PaymentSuccessEvent;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentMethod;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentStatus;
import com.matrix.ecommerce.payment.entity.Payment;
import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

//    @KafkaListener(topics = "payment-initiated", groupId = "payment-group")
    public void handlePaymentInitiated(PaymentOrderRequest paymentRequest, boolean isSuccess) {
        log.info("Inside Payment Event Listener, {}", isSuccess);
        if(isSuccess) {
            Payment payment = Payment.builder()
                    .amount(paymentRequest.getAmount())
                    .userId(paymentRequest.getUserId())
                    .paymentMethod(paymentRequest.getPaymentMethod() == null ? PaymentMethod.CASH : paymentRequest.getPaymentMethod())
                    .paymentStatus(PaymentStatus.SUCCESS).build();
            payment = paymentService.doPayment(payment);
            kafkaTemplate.send("payment-success", new PaymentSuccessEvent(paymentRequest.getOrderId()));
        }
            kafkaTemplate.send("payment-failed", new PaymentFailedEvent(paymentRequest.getOrderId(),0));
    }
}
