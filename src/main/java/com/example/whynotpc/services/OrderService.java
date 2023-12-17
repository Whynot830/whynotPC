package com.example.whynotpc.services;

import com.example.whynotpc.models.response.OrderResponse;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.orders.OrderRepo;
import com.example.whynotpc.persistence.users.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.utils.JPACallHandler.handleCall;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;

    public OrderResponse readAll() {
        return new OrderResponse(200, orderRepo.findAll());
    }

    public OrderResponse read(Integer id) {
        return orderRepo.findById(id)
                .map(order -> new OrderResponse(200, order))
                .orElse(new OrderResponse(404));
    }

    public OrderResponse readByUserId(Integer id) {
        return userRepo.findById(id)
                .map(user -> new OrderResponse(200, orderRepo.findByUserId(user.getId())))
                .orElse(new OrderResponse(404));
    }

    public OrderResponse readByAuthentication(Authentication authentication) {
        if (authentication == null)
            return new OrderResponse(401);

        var user = (User) authentication.getPrincipal();
        return readByUserId(user.getId());
    }

    public OrderResponse delete(Integer id) {
        var response = handleCall(() -> orderRepo.findById(id).map(order -> {
            if (order.getStatus() == CART)
                throw new DataIntegrityViolationException("Cart can't be deleted");
            orderRepo.delete(order);
            return true;
        }).orElseThrow(EntityNotFoundException::new));
        return switch (response.statusCode()) {
            case 200 -> new OrderResponse(204);
            case 400 -> new OrderResponse(400);
            case 404 -> new OrderResponse(404);
            default -> new OrderResponse(500);
        };
    }
}
