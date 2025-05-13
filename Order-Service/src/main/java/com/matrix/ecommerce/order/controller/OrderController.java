package com.matrix.ecommerce.order.controller;

import com.matrix.ecommerce.dtos.dto.dto.order.OrderDto;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import com.matrix.ecommerce.order.entity.Order;
import com.matrix.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    // get all products
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable UUID orderId, @RequestBody @Valid OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderRequest));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");
    }

    @PostMapping("/orders/by-ids")
    public List<OrderRequest> getOrdersByIds(@RequestBody Set<UUID> ids) {
        return orderService.getOrdersByIds(ids); // convert entity → DTO
    }

    @GetMapping("/orders/by-user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

}
