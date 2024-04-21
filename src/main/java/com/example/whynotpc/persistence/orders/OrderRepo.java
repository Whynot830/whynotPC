package com.example.whynotpc.persistence.orders;

import com.example.whynotpc.models.order.Order;
import com.example.whynotpc.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing order entities.
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    /**
     * Finds all orders by user ID.
     *
     * @param userId The ID of the user.
     * @return list of orders associated with the user.
     */
    List<Order> findByUserId(Long userId);

    /**
     * Finds the cart order for a specific user.
     *
     * @param user The user whose cart is being retrieved.
     * @return cart order if found, or an empty optional if not found.
     */
    @Query("select o from Order o where o.user = :user and o.status = 'CART'")
    Optional<Order> findCartByUser(User user);
}
