package com.example.whynotpc.persistence.orders;

import com.example.whynotpc.models.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    OrderItem findByOrderIdAndProductId(Long orderId, Long productId);
}
