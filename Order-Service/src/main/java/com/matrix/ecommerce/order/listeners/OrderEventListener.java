package com.matrix.ecommerce.order.listeners;

import com.matrix.ecommerce.dtos.dto.dto.*;
import com.matrix.ecommerce.dtos.dto.dto.payment.PaymentStatus;
import com.matrix.ecommerce.dtos.dto.dto.product.RestoreProduct;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final String paymentTopic = "payment-initiated";
    private final String productTopic = "product-updated";
    private final String productUpdateFailedTopic = "product-update-failed";
    private final String paymentSuccessTopic = "payment-success";
    private final String paymentFailedTopic = "payment-failed";
    private final String paymentTimeoutTopic = "payment-timeout";
    private final String restoreProductTopic = "restore-product";
    private final String orderGroup = "order-group";

    @KafkaListener(topics = "product-updated", groupId = "order-group")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        PaymentRequest paymentRequest = new PaymentRequest(event.getOrderId(), event.getTotalPrice(), PaymentStatus.PENDING, event.getPaymentMethod());
        log.info("Sending payment-initiated for order {} with total price {}", event.getOrderId(), event.getTotalPrice());
        kafkaTemplate.send("payment-initiated", paymentRequest);
    }

    @KafkaListener(topics = "product-update-failed", groupId = "order-group")
    public void handleProductUpdateFailed(ProductUpdateFailedEvent event) {
        // OrderStatus as CANCELLED
        log.info("Product update failed for order {}. Cancelling order.", event.getOrderId());
        updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED);
    }

    @KafkaListener(topics = "payment-success", groupId = "order-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        // OrderStatus as COMPLETED
        log.info("Payment successful for order {}. Marking order as COMPLETED.", event.getOrderId());
        updateOrderStatus(event.getOrderId(), OrderStatus.COMPLETED);
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

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        RestoreProductEvent restoreProductEvent = buildRestoreEvent(event.getOrderId());
        kafkaTemplate.send("restore-product", restoreProductEvent);
        updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED);
    }

    private void updateOrderStatus(UUID event, OrderStatus orderStatus) {
        Order order = orderRepository.findById(event)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Updating order status for order {} to {}", event, orderStatus);
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
