package com.matrix.ecommerce.payment.event;

import com.matrix.ecommerce.dtos.dto.PaymentFailedEvent;
import com.matrix.ecommerce.dtos.dto.PaymentSuccessEvent;
import com.matrix.ecommerce.dtos.dto.payment.PaymentStatus;
import com.matrix.ecommerce.payment.entity.Payment;
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
    public void handlePaymentInitiated(PaymentOrderRequest paymentRequest) {
        try {
            Payment payment = Payment.builder()
                    .amount(paymentRequest.getAmount())
                    .paymentMethod(paymentRequest.getPaymentMethod() == null ? "CASH" : paymentRequest.getPaymentMethod())
                    .paymentStatus(PaymentStatus.COMPLETED.name()).build();
            payment = paymentService.doPayment(payment);
            log.info("Payment successful for order {}. Sending payment-success event.", paymentRequest.getOrderId());
            kafkaTemplate.send("payment-success", new PaymentSuccessEvent(paymentRequest.getOrderId()));
        } catch (Exception e) {
            kafkaTemplate.send("payment-failed", new PaymentFailedEvent(paymentRequest.getOrderId(),0));
        }
    }
}
