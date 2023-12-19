package com.example.whynotpc.persistence.orders;

import com.example.whynotpc.models.order.Order;
import com.example.whynotpc.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("select o from Order o where o.user = :user and o.status = 'CART'")
    Optional<Order> findCartByUser(User user);
}
