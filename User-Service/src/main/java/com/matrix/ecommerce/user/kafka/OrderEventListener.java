package com.matrix.ecommerce.user.kafka;

import com.matrix.ecommerce.dtos.dto.dto.BalanceUpdateEvent;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
/**
 * OrderEventListener listens to various Kafka events related to orders and processes them accordingly.
 */
@Slf4j
@EnableKafka
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Transactional
public class OrderEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserService userService;

    @KafkaListener(topics = "payment-initiated", groupId = "user-group")
    public void handleUserBalance(BalanceUpdateEvent event) {
        // reduce user balance
        userService.updateBalance(event);
        log.info("User balance updated for order {}. Marking order as COMPLETED.", event.getOrderId());
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "user-group")
    public void handleOrderCreatedAndUpdatedEvent(OrderCreatedEvent event) {
        log.info("Handling event for order ID: {}", event.getOrderId());
        //update orders in user service
        userService.updateOrder(event);
        log.info("Order updated for order ID: {}", event.getOrderId());
    }

}
