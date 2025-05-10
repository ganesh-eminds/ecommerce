package com.matrix.ecommerce.order.listeners;

import com.matrix.ecommerce.dtos.dto.dto.PaymentFailedEvent;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor=@__({@Autowired}))
public class OrderTimeoutScheduler {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderEventListener orderEventListener;

    @Scheduled(fixedRate = 30000)
    public void cancelStaleOrders() {
        LocalDateTime timeout = LocalDateTime.now().minusSeconds(60);
        List<Order> staleOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, timeout);
        log.info("Cancelling {} stale orders", staleOrders.size());

        for (Order order : staleOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            orderEventListener.handlePaymentFailed(new PaymentFailedEvent(order.getId(), 0));
        }
    }
}
 