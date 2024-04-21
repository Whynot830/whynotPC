package com.example.whynotpc.services;

import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.OrderResponse;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.orders.OrderRepo;
import com.example.whynotpc.persistence.users.UserRepo;
import com.example.whynotpc.utils.NoAuthenticationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.OrderResponse.ok;

/**
 * Service class responsible for managing orders.
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;

    /**
     * Retrieves all orders.
     *
     * @return OrderResponse containing all orders
     */
    public OrderResponse readAll() {
        return ok(orderRepo.findAll());
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order to retrieve
     * @return OrderResponse containing the order with the specified ID
     * @throws EntityNotFoundException if no order with the given ID exists
     */
    public OrderResponse read(Long id) {
        return orderRepo.findById(id)
                .map(OrderResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Retrieves orders associated with a user by the user's ID.
     *
     * @param id The ID of the user
     * @return OrderResponse containing orders associated with the user
     * @throws EntityNotFoundException if no user with the given ID exists
     */
    public OrderResponse readByUserId(Long id) {
        userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        return ok(orderRepo.findByUserId(id));
    }

    /**
     * Retrieves orders associated with the authenticated user.
     *
     * @param authentication The authentication object representing the current user
     * @return OrderResponse containing orders associated with the authenticated user
     * @throws NoAuthenticationException if the user is not authenticated
     */
    public OrderResponse readByAuthentication(Authentication authentication) {
        if (authentication == null)
            throw new NoAuthenticationException();

        var user = (User) authentication.getPrincipal();
        return readByUserId(user.getId());
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete
     * @return BasicResponse indicating the success of the operation
     * @throws EntityNotFoundException if no order with the given ID exists
     * @throws IllegalStateException if the order status is 'CART'
     */
    public BasicResponse delete(Long id) {
        var order = orderRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        if (order.getStatus() == CART)
            throw new IllegalStateException("Cart cannot be deleted");
        orderRepo.delete(order);

        return noContent();
    }
}
