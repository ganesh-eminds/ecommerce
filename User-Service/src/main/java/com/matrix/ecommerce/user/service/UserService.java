package com.matrix.ecommerce.user.service;

import com.matrix.ecommerce.dtos.dto.dto.BalanceUpdateEvent;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.payment.PayOrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.user.UserDTO;
import com.matrix.ecommerce.user.client.OrderClient;
import com.matrix.ecommerce.user.client.PaymentClient;
import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostConstruct
    public void init() {
    }

    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setEmail(user.getEmail());
            log.info("User found: {}", userDTO);
            return userDTO;
        }
        return null;
    }

    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(userDTO.getFirstName());
            user.setEmail(userDTO.getEmail());
            userRepository.save(user);
            return userDTO;
        }
        return null;
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public List<OrderRequest> getUserOrders(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderClient.getOrdersByIds(user.getOrderIds());
    }

    public List<PayOrderRequest> getUserPayments(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentClient.getPaymentsByIds(user.getPaymentOrderIds());

    }

    public void updateBalance(BalanceUpdateEvent event) {
        log.info("Inside User Service, {}", event);
        if(event.isSuccess()) {
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            boolean isSuccess = false;
            log.info("User found: {}", user);
            if (user.getBalance() >= event.getAmount()) {
                user.setBalance(user.getBalance() - event.getAmount());
                log.info("User balance updated: {}", user);
                userRepository.save(user);
                isSuccess = true;
            }
                log.info("Sending to user update for order {} with total price {}", event.getOrderId(), event.getAmount());
                kafkaTemplate.send("user-update", new BalanceUpdateEvent(event.getOrderId(), event.getUserId(), event.getAmount(), isSuccess));
        }/* else {
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setBalance(user.getBalance() + event.getAmount());
            userRepository.save(user);
            log.info("Sending to user update for order {} with total price {}", event.getOrderId(), event.getAmount());
            kafkaTemplate.send("user-update", new BalanceUpdateEvent(event.getOrderId(), event.getUserId(), event.getAmount(), false));
        }*/
    }

    public void updateOrder(OrderCreatedEvent event) {
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user != null) {
            user.getOrderIds().add(event.getOrderId());
            userRepository.save(user);
            log.info("User order updated: {}", user);
        }
    }
/*
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .orders(new HashSet<>(orders))
                .payments(new HashSet<>(payments))
                .build();
*/
}
