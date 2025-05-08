package com.matrix.ecommerce.order.listeners;

import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class OrderTimeoutScheduler {

    @NonNull
    private OrderRepository orderRepository;


    @Scheduled(fixedRate = 30000)
    public void cancelStaleOrders() {
        LocalDateTime timeout = LocalDateTime.now().minusSeconds(60);

        List<Order> staleOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, timeout);

        log.info("Cancelling {} stale orders", staleOrders.size());

        for (Order order : staleOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

        }
    }

}
 