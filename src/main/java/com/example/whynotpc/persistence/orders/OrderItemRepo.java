package com.example.whynotpc.persistence.orders;

import com.example.whynotpc.models.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT COALESCE(SUM(oi.product.price), 0) FROM OrderItem oi WHERE oi.order.id = :orderId")
    float getTotalOrderItemsPrice(Integer orderId);

    @Query("""
            SELECT CASE WHEN count(i) > 0 THEN TRUE ELSE FALSE END
            FROM OrderItem i WHERE i.id = :orderId AND i.product.id = :productId
            """)
    boolean existsInUserCart(Integer orderId, Integer productId);

    OrderItem findByOrderIdAndProductId(Integer orderId, Integer productId);
}
