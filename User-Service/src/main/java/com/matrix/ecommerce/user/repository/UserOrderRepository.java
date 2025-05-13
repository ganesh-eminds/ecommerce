package com.matrix.ecommerce.user.repository;

import com.matrix.ecommerce.user.entity.User;
import com.matrix.ecommerce.user.entity.order.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, UUID> {

}
