package com.matrix.ecommerce.product.listener;

import com.matrix.ecommerce.dtos.dto.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.ProductUpdateFailedEvent;
import com.matrix.ecommerce.dtos.dto.ProductUpdatedEvent;
import com.matrix.ecommerce.dtos.dto.RestoreProductEvent;
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
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() >= event.getQuantity()) {
            product.setStock(product.getStock() - event.getQuantity());
            productRepository.save(product);

            ProductUpdatedEvent productUpdatedEvent = new ProductUpdatedEvent(
                    event.getOrderId(),
                    product.getPrice() * event.getQuantity(),
                    event.getQuantity(),
                    event.getPaymentMethod()
            );
            // Save the payment order to the repository
            PaymentOrderRequest paymentOrderRequest = new PaymentOrderRequest(
                    event.getOrderId(),
                    product.getPrice() * event.getQuantity(),
                    "PENDING",
                    event.getPaymentMethod()
            );
            paymentOrderRepository.save(paymentOrderRequest);
            kafkaTemplate.send("product-updated", productUpdatedEvent);
        } else {
            kafkaTemplate.send("product-update-failed", new ProductUpdateFailedEvent(event.getOrderId()));
        }
    }

    @KafkaListener(topics = "restore-product", groupId = "product-group")
    public void handleRestoreProduct(RestoreProductEvent event) {
        // Simplified: Just for demo purposes (normally would need quantity info)
        // For real rollback, quantity info should be stored or passed in RestoreProductEvent
        // Here we assume restoring +1 quantity for demo
        log.info("Restoring product with ID: {}", event.getOrderId());
        Product product = productRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        log.info("Restoring product: {}", product);
        if (product != null) {
            product.setStock(product.getStock() + event.getQuantity());
            productRepository.save(product);
        }
    }
}
