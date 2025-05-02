package com.matrix.ecommerce.payment.event;

import com.matrix.ecommerce.dtos.dto.PaymentFailedEvent;
import com.matrix.ecommerce.dtos.dto.PaymentSuccessEvent;
import com.matrix.ecommerce.payment.entity.Payment;
import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

//    @KafkaListener(topics = "payment-initiated", groupId = "payment-group")
    public void handlePaymentInitiated(PaymentOrderRequest paymentRequest) {
        try {
            Payment payment = Payment.builder()
                    .amount(paymentRequest.getAmount())
                    .paymentMethod(paymentRequest.getPaymentMethod() == null ? "CASH" : paymentRequest.getPaymentMethod())
                    .paymentStatus("PAYMENT_SUCCESS").build();
            payment = paymentService.doPayment(payment);
            kafkaTemplate.send("payment-success", new PaymentSuccessEvent(paymentRequest.getOrderId()));
        } catch (Exception e) {
            kafkaTemplate.send("payment-failed", new PaymentFailedEvent(paymentRequest.getOrderId(),0));
        }
    }
}
