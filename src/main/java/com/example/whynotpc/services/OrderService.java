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

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;

    public OrderResponse readAll() {
        return ok(orderRepo.findAll());
    }

    public OrderResponse read(Long id) {
        return orderRepo.findById(id)
                .map(OrderResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    public OrderResponse readByUserId(Long id) {
        userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        return ok(orderRepo.findByUserId(id));
    }

    public OrderResponse readByAuthentication(Authentication authentication) {
        if (authentication == null)
            throw new NoAuthenticationException();

        var user = (User) authentication.getPrincipal();
        return readByUserId(user.getId());
    }

    public BasicResponse delete(Long id) {
        var order = orderRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        if (order.getStatus() == CART)
            throw new IllegalStateException("Cart cannot be deleted");
        orderRepo.delete(order);

        return noContent();
    }
}
