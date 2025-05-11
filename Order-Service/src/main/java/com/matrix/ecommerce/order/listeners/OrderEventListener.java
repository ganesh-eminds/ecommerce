package com.matrix.ecommerce.order.listeners;

import com.matrix.ecommerce.dtos.dto.dto.*;
import com.matrix.ecommerce.dtos.dto.dto.product.RestoreProduct;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
/**
 * OrderEventListener listens to various Kafka events related to orders and processes them accordingly.
 */
@Slf4j
@RequiredArgsConstructor(onConstructor=@__({@Autowired}))
@EnableKafka
@Transactional
public class OrderEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

/*
    @KafkaListener(topics = "product-updated", groupId = "order-group")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        PaymentRequest paymentRequest = new PaymentRequest(event.getOrderId(), event.getTotalPrice(), PaymentStatus.PENDING, event.getPaymentMethod());
    }
*/

    @KafkaListener(topics = "product-update-failed", groupId = "order-group")
    public void handleProductUpdateFailed(ProductUpdateFailedEvent event) {
        // OrderStatus as CANCELLED
        log.info("Product update failed for order {}. Cancelling order.", event.getOrderId());
        updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED);
    }

    @KafkaListener(topics = "user-update", groupId = "order-group")
    public void handlePayment(BalanceUpdateEvent event) {
        // OrderStatus as COMPLETED
        log.info("Inside the handlePayment of Order Service with {}.", event);
        if(!event.isSuccess()){
            RestoreProductEvent restoreProductEvent = buildRestoreEvent(event.getOrderId());
            kafkaTemplate.send("restore-product", restoreProductEvent);
            updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED);
        } else {
            log.info("Payment successful for order {}. Marking order as COMPLETED.", event.getOrderId());
            updateOrderStatus(event.getOrderId(), OrderStatus.COMPLETED);
        }
    }

    public RestoreProductEvent buildRestoreEvent(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        List<RestoreProduct> restoreProducts = order.getOrderItems().stream()
                .map(item -> {
                    RestoreProduct restoreProduct = new RestoreProduct();
                    restoreProduct.setOrderId(orderId);
                    restoreProduct.setProductId(item.getProductId());
                    restoreProduct.setQuantity(item.getQuantity());
                    return restoreProduct;
                })
                .toList();

        return new RestoreProductEvent(orderId, restoreProducts);
    }

    private void updateOrderStatus(UUID event, OrderStatus orderStatus) {
        Order order = orderRepository.findById(event)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Updating order status for order {} to {}", event, orderStatus);
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
