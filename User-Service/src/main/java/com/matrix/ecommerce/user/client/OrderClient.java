package com.matrix.ecommerce.user.client;

import com.matrix.ecommerce.dtos.dto.dto.order.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {
    @PostMapping("/api/order/orders/by-ids")
    List<OrderRequest> getOrdersByIds(@RequestBody Set<UUID> orderIds);
}
