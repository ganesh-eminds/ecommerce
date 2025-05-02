package com.matrix.ecommerce.order.service;

import com.matrix.ecommerce.dtos.dto.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.payment.PaymentTimeoutEvent;
import com.matrix.ecommerce.order.dto.OrderRequestDto;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private ScheduledExecutorService scheduledExecutorService = java.util.concurrent.Executors.newScheduledThreadPool(5);

    public Order createOrder(OrderRequestDto orderRequestDto) {
        Order order = Order.builder()
                .productId(orderRequestDto.getProductId())
                .quantity(orderRequestDto.getQuantity())
                .paymentMethod(orderRequestDto.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        sendKafkaToUpdate(orderRequestDto, order);

        return order;
    }

    private void sendKafkaToUpdate(OrderRequestDto orderRequestDto, Order order) {
        kafkaTemplate.send("order-created", new OrderCreatedEvent(order.getId(), order.getProductId(), order.getQuantity(), order.getPaymentMethod()));

        // Send timeout message
        PaymentTimeoutEvent timeoutEvent = new PaymentTimeoutEvent(order.getId(), orderRequestDto.getProductId());

        kafkaTemplate.send("payment-timeout", timeoutEvent);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrder(UUID orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setProductId(orderRequestDto.getProductId());
        order.setQuantity(orderRequestDto.getQuantity());
        order.setStatus(OrderStatus.PENDING);

        sendKafkaToUpdate(orderRequestDto, order);

        return orderRepository.save(order);
    }
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if(order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order already cancelled");
        }
        orderRepository.delete(order);
        log.info("Order with ID {} deleted", orderId);
        // Send Kafka message to notify deletion
        kafkaTemplate.send("restore-product", orderId);
        log.info("Kafka message sent for order deletion with ID {}", orderId);
    }
}
