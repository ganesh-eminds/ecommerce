package com.matrix.ecommerce.user.service;

import com.matrix.ecommerce.dtos.dto.dto.BalanceUpdateEvent;
import com.matrix.ecommerce.dtos.dto.dto.exception.UserNotFoundException;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderCreatedEvent;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.order.OrderStatus;
import com.matrix.ecommerce.dtos.dto.dto.payment.PayOrderRequest;
import com.matrix.ecommerce.dtos.dto.dto.user.UserDTO;
import com.matrix.ecommerce.user.client.OrderClient;
import com.matrix.ecommerce.user.client.PaymentClient;
import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.entity.order.UserOrder;
import com.matrix.ecommerce.user.repository.UserOrderRepository;
import com.matrix.ecommerce.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;
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

    public List<OrderRequest> getUserOrders(UUID userId, WebRequest request) {
        request.setAttribute("userId", userId, RequestAttributes.SCOPE_REQUEST);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return orderClient.getOrdersByIds(getAllUserOrderIds(user));
    }

    private Set<UUID> getAllUserOrderIds(User user) {
        user.getUserOrders()
                .forEach(order -> {
                    log.info("User order found: {}", order.getOrderId());
                });
        return user.getUserOrders().stream()
                .map(UserOrder::getOrderId)
                .collect(Collectors.toSet());
    }

    public List<PayOrderRequest> getUserPayments(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentClient.getPaymentsByIds(user.getPaymentOrderIds());

    }

    public void updateBalance(BalanceUpdateEvent event) {
        log.info("Inside User Service, {}", event);

        if (event.isSuccess()) {
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isSuccess = false;

            log.info("User found: {}", user);

            if (user.getBalance() >= event.getAmount()) {
                user.setBalance(user.getBalance() - event.getAmount());

//                UserOrder userOrder = new UserOrder(event.getOrderId(), user, OrderStatus.COMPLETED);

//                userOrder.setUser(user); // ensure bi-directional consistency
                UserOrder userOrder = UserOrder.builder()
                        .orderStatus(OrderStatus.COMPLETED)
                        .orderId(event.getOrderId())
                        .user(user)
                        .build();
                userOrderRepository.save(userOrder); // save the order first
                log.info("User order found: {}", userOrder);
//                user.getUserOrders().add(userOrder);
                log.info("User balance updated: {}", user);

                userRepository.save(user); // avoid merge unless needed

                isSuccess = true;
            }

            log.info("Sending to user update for order {} with total price {}", event.getOrderId(), event.getAmount());

            kafkaTemplate.send("user-update", new BalanceUpdateEvent(
                    event.getOrderId(), event.getUserId(), event.getAmount(), isSuccess
            ));
            log.info("User balance updated: {}", user);
        }
    }


/*    public void updateBalance(BalanceUpdateEvent event) {
        log.info("Inside User Service, {}", event);
        if (event.isSuccess()) {
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            boolean isSuccess = false;
            log.info("User found: {}", user);
            if (user.getBalance() >= event.getAmount()) {
                user.setBalance(user.getBalance() - event.getAmount());
                user.setUserOrders(List.of(new UserOrder(event.getOrderId(), user, OrderStatus.COMPLETED)));
                log.info("User balance updated: {}", user);
                userRepository.save(user);
                isSuccess = true;
            }
            log.info("Sending to user update for order {} with total price {}", event.getOrderId(), event.getAmount());
            kafkaTemplate.send("user-update", new BalanceUpdateEvent(event.getOrderId(), event.getUserId(), event.getAmount(), isSuccess));
        }*//* else {
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setBalance(user.getBalance() + event.getAmount());
            userRepository.save(user);
            log.info("Sending to user update for order {} with total price {}", event.getOrderId(), event.getAmount());
            kafkaTemplate.send("user-update", new BalanceUpdateEvent(event.getOrderId(), event.getUserId(), event.getAmount(), false));
        }
    }*/

/*
    public void updateOrder(OrderCreatedEvent event) {
        log.info("Inside User Service, {}", event);
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user != null) {
            log.error("User {} found with ID: {}", user, event.getUserId());
            user.setUserOrders(setUserOrder(event.getOrderId(), event.getOrderStatus(), user));
            userRepository.save(user);
            log.info("User order updated: {}", user);
        }
    }

    private List<UserOrder> setUserOrder(UUID orderId, OrderStatus status, User user) {
        UserOrder userOrder = new UserOrder();
        userOrder.setOrderId(orderId);
        userOrder.setUser(user);
        userOrder.setOrderStatus(status);
        userOrderRepository.save(userOrder);
        return List.of(userOrder);
    }
*/

    public void updateOrder(OrderCreatedEvent event) {
        log.info("Inside User Service, {}", event);
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user != null) {
            log.info("User found with ID: {}", event.getUserId());

            // Create new UserOrder
            UserOrder userOrder = new UserOrder();
            userOrder.setOrderId(event.getOrderId());
            userOrder.setOrderStatus(event.getOrderStatus());
            userOrder.setUser(user); // set owning side

            // Append to existing list
            if (user.getUserOrders() == null) {
                user.setUserOrders(new ArrayList<>());
            }
            user.getUserOrders().add(userOrder);

            // Save user (cascades save for UserOrder)
            userRepository.save(user);

            log.info("User order updated: {}", user);
        }
    }


    public boolean hasUserPlacedFirstOrder(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        List<UserOrder> orders = user.getUserOrders();
        if (orders == null || orders.isEmpty()) {
            return false; // No orders means user hasn't placed any
        }

        return orders.stream()
                .anyMatch(order -> order.getOrderStatus() == OrderStatus.COMPLETED);
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
