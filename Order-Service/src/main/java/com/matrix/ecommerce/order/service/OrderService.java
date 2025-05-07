package com.matrix.ecommerce.order.service;

import com.matrix.ecommerce.dtos.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.exception.ValidationException;
import com.matrix.ecommerce.dtos.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.order.dto.OrderRequest;
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

        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .paymentMethod(orderRequest.getPaymentMethod())
                .build();

        // Map and attach OrderItems
        Order finalOrder = order;
        List<OrderItem> orderItems = orderRequest.getProducts().stream()
                .map(productRequest -> OrderItem.builder()
                        .productId(productRequest.getProductId())
                        .quantity(productRequest.getQuantity())
                        .price(productRequest.getPrice())
                        .order(finalOrder)
                        .build())
                .toList();

        order.setOrderItems(orderItems);

        order = orderRepository.save(order); // JPA will cascade and save OrderItems

        // Optional: Kafka or event logic
        sendKafkaToOrderProducts(order, orderItems);

        return ResponseEntity.ok(order);
    }

    public static String generateKey(String prefix, String uniqueId) {
        return prefix + "-" + uniqueId;
    }

    public int calculatePartition(String productId, int totalPartitions) {
        int hash = productId.hashCode();
        return Math.abs(hash) % totalPartitions;
    }

    public void sendKafkaToOrderProducts(Order order, List<OrderItem> orderItems) {

        String key = generateKey("order", UUID.randomUUID().toString());

        int partitionNo = calculatePartition(String.valueOf(orderItems.get(0).getProductId()), 10);

        kafkaTemplate.send("order-created", Integer.valueOf(partitionNo), key, new OrderCreatedEvent(
                order.getId(),
                orderItems.stream()
                        .map(orderItem -> ProductDetails.builder()
                                .productId(orderItem.getProductId())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build())
                        .toList(),
                order.getPaymentMethod(),
                "order-created"
        ));
    }

    public void sendKafkaToUpdateOrderProducts(Order order, List<OrderItem> oldOrderItems, List<OrderItem> newOrderItems) {

        List<ProductDetails> productDetails = newOrderItems.stream()
                .map(newItem -> {
                    OrderItem oldItem = oldOrderItems.stream()
                            .filter(old -> old.getProductId().equals(newItem.getProductId()))
                            .findFirst()
                            .orElse(null);

                    int quantityDifference = newItem.getQuantity() - (oldItem != null ? oldItem.getQuantity() : 0);

                    return ProductDetails.builder()
                            .productId(newItem.getProductId())
                            .quantity(Integer.valueOf(quantityDifference))
                            .price(newItem.getPrice())
                            .build();
                })
                .toList();

        kafkaTemplate.send("order-updated", new OrderCreatedEvent(
                order.getId(),
                productDetails,
                order.getPaymentMethod(),
                "order-updated"
        ));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException(new ExceptionDto("ORDER_NOT_FOUND","404", "BAD REQUEST", "Order Not Found")));
    }

    public Order updateOrder(UUID orderId, OrderRequest orderRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException(new ExceptionDto("ORDER_NOT_FOUND","404", "BAD REQUEST", "Order Not Found")));

        // Store old items for comparison
        List<OrderItem> oldOrderItems = new ArrayList<>(order.getOrderItems());

        // Clear old items (triggers orphan removal)
        order.getOrderItems().clear();

        // Map and set new items
        List<OrderItem> updatedItems = orderRequest.getProducts().stream()
                .map(productRequest -> OrderItem.builder()
                        .productId(productRequest.getProductId())
                        .quantity(productRequest.getQuantity())
                        .price(productRequest.getPrice())
                        .order(order) // set parent reference
                        .build())
                .toList();

        order.getOrderItems().addAll(updatedItems); // safely add new items
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Send Kafka event for updated order
        sendKafkaToUpdateOrderProducts(savedOrder, oldOrderItems, updatedItems);

        return savedOrder;
    }

    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ValidationException(new ExceptionDto("ORDER_NOT_FOUND","404", "BAD REQUEST", "Order Not Found")));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException(new ExceptionDto("ORDER_ALREADY_CANCELLED","404", "BAD REQUEST", "Order Already Cancelled"));
        }
        orderRepository.delete(order);
        log.info("Order with ID {} deleted", orderId);
        // Send Kafka message to notify deletion
        kafkaTemplate.send("restore-product", orderId);
        log.info("Kafka message sent for order deletion with ID {}", orderId);
    }
}