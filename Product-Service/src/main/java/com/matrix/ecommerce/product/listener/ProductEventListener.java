package com.matrix.ecommerce.product.listener;

import com.matrix.ecommerce.dtos.dto.ProductUpdateFailedEvent;
import com.matrix.ecommerce.dtos.dto.ProductUpdatedEvent;
import com.matrix.ecommerce.dtos.dto.RestoreProductEvent;
import com.matrix.ecommerce.dtos.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.product.ProductDetails;
import com.matrix.ecommerce.product.entity.PaymentOrderRequest;
import com.matrix.ecommerce.product.entity.Product;
import com.matrix.ecommerce.product.repository.PaymentOrderRepository;
import com.matrix.ecommerce.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
@Slf4j
public class ProductEventListener {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated1(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer1");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated2(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer2");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated3(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer3");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated4(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer4");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated5(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer5");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated6(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer6");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated7(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer7");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated8(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer8");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated9(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer9");
    }

    @KafkaListener(topics = {"order-created", "order-updated"}, groupId = "product-group")
    public void handleOrderCreatedOrUpdated10(OrderCreatedEvent event) {
        try {
            handleOrderCreatedAndUpdatedEvent(event);
        } catch (RuntimeException ex) {
            log.error("Error processing event for order ID: {}. Sending to dead-letter topic.", event.getOrderId(), ex);
        }
        System.out.println("consumer10");
    }

    public void handleOrderCreatedAndUpdatedEvent(OrderCreatedEvent event) {

        log.info("Handling event for order ID: {}", event.getOrderId());

        double totalPrice = 0.0;

        for (ProductDetails productDetails : event.getProductDetails()) {
            Product product = productRepository.findById(productDetails.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found for ID: " + productDetails.getProductId()));

            // Adjust stock based on the event type
            if ("order-created".equals(event.getEventType())) {
                if (product.getStock() >= productDetails.getQuantity()) {
                    log.info("Product Stock");
                    product.setStock(product.getStock() - productDetails.getQuantity());
                    totalPrice += productDetails.getPrice() * productDetails.getQuantity();
                    log.info("Product Stock after reduce");
                } else {
                    kafkaTemplate.send("product-update-failed", new ProductUpdateFailedEvent(event.getOrderId()));
                    log.info("Product stock update failed event");
                    return;
                }
            } else if ("order-updated".equals(event.getEventType())) {
                totalPrice = 0.0;
                log.info("Product Stock update");
                product.setStock(product.getStock() - productDetails.getQuantity());
                totalPrice += productDetails.getPrice() * productDetails.getQuantity();
                log.info("Product Stock after reduce update");
            }

            productRepository.save(product);

            log.info("Adjusted stock for product ID: {} by quantity: {}", productDetails.getProductId(), productDetails.getQuantity());
        }

        log.info("Total price of the order ID {} is: {}", event.getOrderId(), totalPrice);

        // Save the payment order to the repository
        PaymentOrderRequest paymentOrderRequest = new PaymentOrderRequest(
                event.getOrderId(),
                totalPrice,
                "PENDING",
                event.getPaymentMethod()
        );
        paymentOrderRepository.save(paymentOrderRequest);

        // Send ProductUpdatedEvent for both creation and update
        ProductUpdatedEvent productUpdatedEvent = new ProductUpdatedEvent(
                event.getOrderId(),
                totalPrice,
                event.getProductDetails().stream().mapToInt(ProductDetails::getQuantity).sum(),
                event.getPaymentMethod()
        );
        kafkaTemplate.send("product-updated", productUpdatedEvent);
        log.info("Sent product updated event");
    }

    @KafkaListener(topics = "restore-product", groupId = "product-group")
    public void handleRestoreProduct(RestoreProductEvent event) {
        log.info("Restoring product with ID: {}", event.getOrderId());
        Product product = productRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + event.getOrderId()));

        // Restore the stock
        product.setStock(product.getStock() + event.getQuantity());
        productRepository.save(product);
        log.info("Restored stock for product ID");
    }

//    @KafkaListener(topics = "order-created", groupId = "product-group")
//    public void handleOrderCreated(OrderCreatedEvent event) {
//        Product product = productRepository.findById(event.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        if (product.getStock() >= event.getQuantity()) {
//            product.setStock(product.getStock() - event.getQuantity());
//            productRepository.save(product);
//
//            ProductUpdatedEvent productUpdatedEvent = new ProductUpdatedEvent(
//                    event.getOrderId(),
//                    product.getPrice() * event.getQuantity(),
//                    event.getQuantity(),
//                    event.getPaymentMethod()
//            );
//            // Save the payment order to the repository
//            PaymentOrderRequest paymentOrderRequest = new PaymentOrderRequest(
//                    event.getOrderId(),
//                    product.getPrice() * event.getQuantity(),
//                    "PENDING",
//                    event.getPaymentMethod()
//            );
//            paymentOrderRepository.save(paymentOrderRequest);
//            kafkaTemplate.send("product-updated", productUpdatedEvent);
//        } else {
//            kafkaTemplate.send("product-update-failed", new ProductUpdateFailedEvent(event.getOrderId()));
//        }
//    }

//    @KafkaListener(topics = "   restore-product", groupId = "product-group")
//    public void handleRestoreProduct(RestoreProductEvent event) {
//        // Simplified: Just for demo purposes (normally would need quantity info)
//        // For real rollback, quantity info should be stored or passed in RestoreProductEvent
//        // Here we assume restoring +1 quantity for demo
//        log.info("Restoring product with ID: {}", event.getOrderId());
//        Product product = productRepository.findById(event.getOrderId())
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//        log.info("Restoring product: {}", product);
//        if (product != null) {
//            product.setStock(product.getStock() + event.getQuantity());
//            productRepository.save(product);
//        }
//    }
}
