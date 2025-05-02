package com.matrix.ecommerce.order.service;

import com.matrix.ecommerce.dtos.dto.exception.ExceptionDto;
import com.matrix.ecommerce.dtos.dto.exception.ValidationException;
import com.matrix.ecommerce.dtos.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.payment.PaymentTimeoutEvent;
import com.matrix.ecommerce.order.dto.OrderRequestDto;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static com.matrix.ecommerce.dtos.util.ErrorCodes.PRODUCT_OUT_OF_STOCK;
import static com.matrix.ecommerce.dtos.util.ErrorCodes.PRODUCT_OUT_OF_STOCK_STATUS;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final String PRODUCT_OUT_OF_STOCK_DESCRIPTION = "Product is out of stock";
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private ScheduledExecutorService scheduledExecutorService = java.util.concurrent.Executors.newScheduledThreadPool(5);
    private final RestTemplate restTemplate;

    public ResponseEntity<Order> createOrder(OrderRequestDto orderRequestDto) {
        // Validate the order request
        log.info("Validating order request: {}", orderRequestDto);
        if (orderRequestDto.getProductId() == null || orderRequestDto.getQuantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // Check if the product is available in stock
        // add logs for exception handling
        log.info("Checking product stock for product ID: {}", orderRequestDto.getProductId());
        String productServiceURL = "http://PRODUCT-SERVICE/api/product/check-stock/" + orderRequestDto.getProductId();
        Integer stock = 0;
        try {
            stock = restTemplate.getForObject(productServiceURL, Integer.class);
        } catch (Exception e) {
            log.error("Error while checking product stock: {}", e.getMessage());
            ExceptionDto exceptionDto = new ExceptionDto(PRODUCT_OUT_OF_STOCK, PRODUCT_OUT_OF_STOCK_STATUS, "BAD REQUEST", PRODUCT_OUT_OF_STOCK_DESCRIPTION);
            throw new ValidationException(exceptionDto);
        }
        if (stock == 0 || stock < orderRequestDto.getQuantity()) {
            log.error("Product with ID {} is not available in stock", orderRequestDto.getProductId());
            ExceptionDto exceptionDto = new ExceptionDto(PRODUCT_OUT_OF_STOCK, PRODUCT_OUT_OF_STOCK_STATUS, "BAD REQUEST", PRODUCT_OUT_OF_STOCK_DESCRIPTION);
            throw new ValidationException(exceptionDto);
        }

        Order order = Order.builder()
                .productId(orderRequestDto.getProductId())
                .quantity(orderRequestDto.getQuantity())
                .paymentMethod(orderRequestDto.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        sendKafkaToUpdate(orderRequestDto, order);

        return ResponseEntity.ok(order);
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
