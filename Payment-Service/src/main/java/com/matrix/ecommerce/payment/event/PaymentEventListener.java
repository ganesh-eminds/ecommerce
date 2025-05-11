package com.matrix.ecommerce.payment.event;

import com.matrix.ecommerce.dtos.dto.dto.BalanceUpdateEvent;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentStatus;
import com.matrix.ecommerce.payment.entity.PaymentOrderRequest;
import com.matrix.ecommerce.payment.repository.PaymentOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableKafka
@Transactional
public class PaymentEventListener {

    private final PaymentOrderRepository paymentOrderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "user-update", groupId = "payment-group")
    public void handlePaymentInitiated(BalanceUpdateEvent balanceUpdateEvent) {
        log.info("Inside Payment Event Listener, {}", balanceUpdateEvent);
        PaymentOrderRequest paymentOrder = paymentOrderRepository.findById(balanceUpdateEvent.getOrderId()).orElse(null);
        if (balanceUpdateEvent.isSuccess()) {
            paymentOrder.setPaymentStatus(PaymentStatus.SUCCESS);
        } else {
            paymentOrder.setPaymentStatus(PaymentStatus.FAILED);
        }
    }
}
