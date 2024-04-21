package com.example.whynotpc.persistence.orders;

import com.example.whynotpc.models.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing order item entities.
 */
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    /**
     * Finds an order item by its order and product IDs.
     *
     * @param orderId  The ID of the order.
     * @param productId The ID of the product.
     * @return order item if found, or null if not found.
     */
    OrderItem findByOrderIdAndProductId(Long orderId, Long productId);
}
