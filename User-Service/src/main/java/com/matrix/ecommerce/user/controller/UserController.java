package com.matrix.ecommerce.user.controller;

import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.payment.PayOrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.user.UserDTO;
import com.matrix.ecommerce.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    // Get user by ID
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    // Update user
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    // Get user orders
    @GetMapping("/{id}/orders")
    public List<OrderRequest> getUserOrders(@PathVariable UUID id) {
        return userService.getUserOrders(id);
    }

    // Get user payments
    @GetMapping("/{id}/payments")
    public List<PayOrderRequest> getUserPayments(@PathVariable UUID id) {
        return userService.getUserPayments(id);
    }
}