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

    @KafkaListener(topics = "order-created", groupId = "product-group")
    public void handleOrderCreated(OrderCreatedEvent event) {

        log.info("Inside Product Event Listener");
        double totalPrice = 0.0;

        for (ProductDetails productDetails : event.getProductDetails()) {
            Product product = productRepository.findById(productDetails.getProductId()).get();

            if (product.getStock() >= productDetails.getQuantity()) {
                // Update stock
                product.setStock(product.getStock() - productDetails.getQuantity());
                productRepository.save(product);

                // Calculate total price for this product
                totalPrice += product.getPrice() * productDetails.getQuantity();

                log.info("Calculating the total price of the order");
            } else {
                // Send ProductUpdateFailedEvent if stock is insufficient
                kafkaTemplate.send("product-update-failed", new ProductUpdateFailedEvent(event.getOrderId()));
                log.info("Product stock update failed event");
                return; // Exit the method if any product fails
            }
        }

        log.info("Calculated the total price of the order");

        // Create and send ProductUpdatedEvent
        ProductUpdatedEvent productUpdatedEvent = new ProductUpdatedEvent(
                event.getOrderId(),
                totalPrice,
                event.getProductDetails().stream().mapToInt(ProductDetails::getQuantity).sum(),
                event.getPaymentMethod()
        );
        kafkaTemplate.send("product-updated", productUpdatedEvent);
        log.info("Sending product updated event");

        log.info("Sending payment initiated event");
        // Save PaymentOrderRequest
        PaymentOrderRequest paymentOrderRequest = new PaymentOrderRequest(
                event.getOrderId(),
                totalPrice,
                "PENDING",
                event.getPaymentMethod()
        );
        paymentOrderRepository.save(paymentOrderRequest);

        log.info("Updated the Product stock after receiving the order");
    }

    @KafkaListener(topics = "restore-product", groupId = "product-group")
    public void handleRestoreProduct(RestoreProductEvent event) {
        log.info("Restoring product with ID: {}", event.getOrderId());
        Product product = productRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + event.getOrderId()));

        // Restore the stock
        product.setStock(product.getStock() + event.getQuantity());
        productRepository.save(product);
        log.info("Restored stock for product ID: {} by quantity: {}", event.getOrderId(), event.getQuantity());
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
