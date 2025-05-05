package com.matrix.ecommerce.order.service;

import com.matrix.ecommerce.dtos.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.exception.ValidationException;
import com.matrix.ecommerce.dtos.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.payment.PaymentTimeoutEvent;
import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.order.dto.InventoryEvent;
import com.matrix.ecommerce.order.dto.OrderRequest;
import com.matrix.ecommerce.order.dto.OrderRequestDto;
import com.matrix.ecommerce.order.dto.ProductRequest;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderItem;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final String PRODUCT_OUT_OF_STOCK_DESCRIPTION = "Product is out of stock";
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ScheduledExecutorService scheduledExecutorService = java.util.concurrent.Executors.newScheduledThreadPool(5);
    private final RestTemplate restTemplate;

    public ResponseEntity<Order> placeOrder(OrderRequest orderRequest) {

        List<OrderItem> orderItems = orderRequest.getProducts().stream()
                .map(productRequest -> OrderItem.builder()
                        .productId(productRequest.getProductId())
                        .quantity(productRequest.getQuantity())
                        .price(productRequest.getPrice())
                        .build())
                .toList();

        Order order = Order.builder()
                .orderItems(orderItems)
                .status(OrderStatus.PENDING)
                .paymentMethod(orderRequest.getPaymentMethod())
                .build();

        order = orderRepository.save(order);

        // Send Kafka event or any other post-order logic
        sendKafkaToOrderProducts(order, orderItems);

        return ResponseEntity.ok(order);

    }

    public void sendKafkaToOrderProducts(Order order, List<OrderItem> orderItems) {

        kafkaTemplate.send("order-created", new OrderCreatedEvent(order.getId(),
                orderItems.stream()
                        .map(orderItem -> ProductDetails.builder()
                                .productId(orderItem.getProductId())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build())
                        .toList(), order.getPaymentMethod()));

        // Send timeout message
                //PaymentTimeoutEvent timeoutEvent = new PaymentTimeoutEvent(order.getId(), order.getOrderItems().stream()
        //                .map(OrderItem::getProductId)
        //                .collect(Collectors.toList()));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrder(UUID orderId, OrderRequestDto orderRequestDto) {
        // Fetch the existing order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Map OrderRequestDto to OrderItem
        OrderItem orderItem = OrderItem.builder()
                .productId(orderRequestDto.getProductId())
                .quantity(orderRequestDto.getQuantity())
                .price(0.0) // Set price if available in OrderRequestDto
                .order(order)
                .build();

        // Update the order's orderItems list
        order.setOrderItems(List.of(orderItem));
        order.setStatus(OrderStatus.PENDING);

        // Save the updated order
        order = orderRepository.save(order);

        // Send Kafka event
        sendKafkaToOrderProducts(order, order.getOrderItems());

        return order;
    }

    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order already cancelled");
        }
        orderRepository.delete(order);
        log.info("Order with ID {} deleted", orderId);
        // Send Kafka message to notify deletion
        kafkaTemplate.send("restore-product", orderId);
        log.info("Kafka message sent for order deletion with ID {}", orderId);
    }
}