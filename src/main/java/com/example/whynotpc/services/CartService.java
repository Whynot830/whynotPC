package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.OrderDTO;
import com.example.whynotpc.models.order.Order;
import com.example.whynotpc.models.order.OrderItem;
import com.example.whynotpc.models.response.CartResponse;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.orders.OrderItemRepo;
import com.example.whynotpc.persistence.orders.OrderRepo;
import com.example.whynotpc.persistence.products.ProductRepo;
import com.example.whynotpc.utils.JPACallHandler.Result;
import com.example.whynotpc.utils.NoAuthenticationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.models.order.OrderStatus.COMPLETED;
import static com.example.whynotpc.utils.JPACallHandler.handleCall;

@Service
@RequiredArgsConstructor
public class CartService {
    private final OrderRepo orderRepo;
    private final EmailService emailService;
    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;

    private User getUser(Authentication authentication) {
        if (authentication == null)
            throw new NoAuthenticationException();
        return (User) authentication.getPrincipal();
    }

    private Order getCart(Authentication authentication) {
        var user = getUser(authentication);
        return orderRepo.findCartByUser(user).orElseThrow(IllegalArgumentException::new);
    }

    private OrderDTO toDto(Order order) {
        return new OrderDTO(order.getId(), order.getStatus().name(),
                order.getTotal(), order.getItems(), order.getUser().getId());
    }

    private CartResponse getCartResponse(Result<Order> response) {
        return switch (response.statusCode()) {
            case 200 -> new CartResponse(200, toDto(response.result()));
            case 400 -> new CartResponse(400);
            case 401 -> new CartResponse(401);
            case 404 -> new CartResponse(404);
            default -> new CartResponse(500);
        };
    }

    public CartResponse read(Authentication authentication) {
        var response = handleCall(() -> getCart(authentication));
        return getCartResponse(response);
    }

    public CartResponse checkOut(Authentication authentication) {
        var response = handleCall(() -> {
            var cart = getCart(authentication);
            var user = getUser(authentication);

            if (cart.getItems().isEmpty())
                throw new IllegalStateException("Checking out with an empty cart is not allowed");

            cart.setStatus(COMPLETED);
            return orderRepo.save(Order.builder()
                    .status(CART)
                    .total(0.0f)
                    .items(Collections.emptyList())
                    .user(user)
                    .build());
        });
        return getCartResponse(response);
    }

    public CartResponse addItem(Integer productId, Authentication authentication) {
        var response = handleCall(() -> {
            var cart = getCart(authentication);
            var product = productRepo.findById(productId).orElseThrow(EntityNotFoundException::new);
            var item = orderItemRepo.findByOrderIdAndProductId(cart.getId(), productId);

            if (item != null) item.setQuantity(item.getQuantity() + 1);
            else item = OrderItem.builder().quantity(1).product(product).order(cart).build();

            orderItemRepo.save(item);

            return orderRepo.save(cart);
        });
        return getCartResponse(response);
    }


    public CartResponse updateItemQuantity(Integer itemId, Integer quantity, Authentication authentication) {
        var response = handleCall(() -> {
            var cart = getCart(authentication);
            var item = orderItemRepo.findById(itemId).orElseThrow(EntityNotFoundException::new);

            if (quantity < 1)
                throw new IllegalArgumentException("Quantity must be greater than zero");

            item.setQuantity(quantity);
            orderItemRepo.save(item);

            return orderRepo.save(cart);
        });
        return getCartResponse(response);
    }

    public CartResponse deleteItem(Integer itemId, Authentication authentication) {
        var response = handleCall(() -> {
            var cart = getCart(authentication);
            var item = orderItemRepo.findById(itemId).orElseThrow(EntityNotFoundException::new);
            orderItemRepo.delete(item);

            return cart;
        });
        return getCartResponse(response);
    }

    public CartResponse clearCart(Authentication authentication) {
        var response = handleCall(() -> {
            var cart = getCart(authentication);
            var items = cart.getItems();
            cart.setItems(Collections.emptyList());
            orderItemRepo.deleteAll(items);

            return cart;
        });
        return getCartResponse(response);
    }
}
