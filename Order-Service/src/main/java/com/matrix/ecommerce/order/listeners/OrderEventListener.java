package com.matrix.ecommerce.order.listeners;

import com.matrix.ecommerce.dtos.dto.*;
import com.matrix.ecommerce.dtos.dto.payment.PaymentTimeoutEvent;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.entity.OrderItem;
import com.matrix.ecommerce.order.entity.OrderStatus;
import com.matrix.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
/**
 * OrderEventListener listens to various Kafka events related to orders and processes them accordingly.
 */
@Slf4j
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
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
        PaymentRequest paymentRequest = new PaymentRequest(event.getOrderId(), event.getTotalPrice(), "PAYMENT_PENDING", event.getPaymentMethod());
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

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // OrderStatus as CANCELLED
        log.info("Payment failed for order {}. Cancelling order.", event.getOrderId());
        updateOrderStatus(event.getOrderId(), OrderStatus.CANCELLED);
        kafkaTemplate.send("restore-product", new RestoreProductEvent(event.getOrderId(), event.getQuantity()));
    }

    private void updateOrderStatus(UUID event, OrderStatus orderStatus) {
        Order order = orderRepository.findById(event)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        log.info("Updating order status for order {} to {}", event, orderStatus);
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    //@KafkaListener(topics = "payment-timeout", groupId = "order-group")
//    public void handlePaymentTimeout(PaymentTimeoutEvent event) {
//        long time = System.currentTimeMillis();
//        log.info("Scheduling payment timeout 600 seconds for order ID: {}", event.getOrderId());
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("Payment total schedule time: {}", System.currentTimeMillis() - time);
//
//        Optional<Order> orderOpt = orderRepository.findById(event.getOrderId());
//        if (orderOpt.isPresent()) {
//            Order order = orderOpt.get();
//            if (order.getStatus() == OrderStatus.PENDING) {
//                // Update order status to CANCELLED
//                log.info("Payment timeout for order {}. Cancelling order.", order.getId());
//                order.setStatus(OrderStatus.CANCELLED);
//                orderRepository.save(order);
//                log.info("Order {} cancelled due to payment timeout", order.getId());
//
//                // Send a RestoreProductEvent for each OrderItem
//                for (OrderItem orderItem : order.getOrderItems()) {
//                    RestoreProductEvent restoreProductEvent = new RestoreProductEvent(
//                            orderItem.getProductId(),
//                            orderItem.getQuantity()
//                    );
//                    kafkaTemplate.send(restoreProductTopic, restoreProductEvent);
//                    log.info("Product restored for product ID: {}", orderItem.getProductId());
//                }
//            }
//        }
//    }

//    @KafkaListener(topics = "payment-timeout", groupId = "order-group")
//    public void handlePaymentTimeout(PaymentTimeoutEvent event) {
//        long time = System.currentTimeMillis();
//        log.info("Scheduling payment timeout 600 seconds for order ID: {}", event.getOrderId());
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("Payment total schedule time: {}", System.currentTimeMillis() - time);
//        Optional<Order> orderOpt = orderRepository.findById(event.getOrderId());
//        if (orderOpt.isPresent()) {
//            Order order = orderOpt.get();
//            if (order.getStatus() == OrderStatus.PENDING) {
//                // Update order status to CANCELLED
//                log.info("Payment timeout for order {}. Cancelling order.", order.getId());
//                order.setStatus(OrderStatus.CANCELLED);
//                orderRepository.save(order);
//                log.info("Order {} cancelled due to payment timeout", order.getId());
//                // Send a message to restore the product
//                RestoreProductEvent restoreProductEvent = new RestoreProductEvent(order.getProductId(),order.getQuantity());
//                kafkaTemplate.send(restoreProductTopic, restoreProductEvent);
//                log.info("Product restored for order {}", order.getId());
//            }
//        }
//    }
}
