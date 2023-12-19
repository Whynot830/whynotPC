package com.example.whynotpc.services;

import com.example.whynotpc.models.order.Order;
import com.example.whynotpc.models.order.OrderItem;
import com.example.whynotpc.models.response.CartResponse;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.orders.OrderItemRepo;
import com.example.whynotpc.persistence.orders.OrderRepo;
import com.example.whynotpc.persistence.products.ProductRepo;
import com.example.whynotpc.utils.NoAuthenticationException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.models.order.OrderStatus.COMPLETED;
import static com.example.whynotpc.models.response.CartResponse.ok;

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
        return orderRepo.findCartByUser(user).orElseThrow(IllegalStateException::new);
    }

    public CartResponse read(Authentication authentication) {
        return ok(getCart(authentication));
    }

    public CartResponse checkOut(Authentication authentication) {
        var cart = getCart(authentication);
        var user = cart.user;

        var items = cart.getItems();
        if (items.isEmpty())
            throw new IllegalStateException("Checking out with an empty cart is not allowed");

        StringBuilder sb = new StringBuilder();
        sb
                .append("<div style=\"display: flex; align-content: center; flex-direction: column; gap: 0.5rem; width: 100%\">")
                .append("<h2>Congratulations, you've confirmed your order!</h2>")
                .append("<table style=\"sp\">")
                .append("<thead><tr>")
                .append("<th colspan=\"4\"><h3>Ordered items</h3></th>")
                .append("</tr></thead>");

        for (var item : items) {
            var product = item.getProduct();
            sb
                    .append("<tr>")
                    .append("<td style=\"padding: 0; width: 50%\"><h4 style=\"margin: 8px 0\">").append(product.getTitle()).append("</h4></td>")
                    .append("<td style=\"padding: 0\"><h4 style=\"margin: 8px 0\">").append(product.getCategory().getName().toUpperCase()).append("</h4></td>")
                    .append("<td style=\"padding: 0\"><h4 style=\"margin: 8px 0\">").append(item.getQuantity()).append(" pcs.</h4></td>")
                    .append("<td style=\"padding: 0\"><h4 style=\"margin: 8px 0\">USD $ ").append(product.getPrice()).append("</h4></td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>")
                .append("<h3>Total: USD $ ").append(cart.getTotal()).append("</h3>")
                .append("<h4>Best regards,<br/>WHYNOTPC</h4></div>");

        try {
            emailService.sendMail(user.getEmail(), sb.toString());
        } catch (MessagingException ex) {
            System.out.println(ex.getMessage());
        }

        cart.setStatus(COMPLETED);
        cart = orderRepo.save(Order.builder()
                .status(CART)
                .total(BigDecimal.valueOf(0))
                .items(Collections.emptyList())
                .user(user)
                .build());

        return ok(cart);
    }

    public CartResponse addItem(Long productId, Authentication authentication) {
        var cart = getCart(authentication);
        var product = productRepo.findById(productId).orElseThrow(EntityNotFoundException::new);
        var item = orderItemRepo.findByOrderIdAndProductId(cart.getId(), productId);

        if (item != null) item.setQuantity(item.getQuantity() + 1);
        else item = OrderItem.builder().quantity(1).product(product).order(cart).build();

        orderItemRepo.save(item);
        orderRepo.save(cart);

        return ok(cart);
    }


    public CartResponse updateItemQuantity(Long itemId, Integer quantity, Authentication authentication) {
        var cart = getCart(authentication);
        var item = orderItemRepo.findById(itemId).orElseThrow(EntityNotFoundException::new);

        if (quantity < 1)
            throw new IllegalArgumentException("Quantity must be greater than zero");

        item.setQuantity(quantity);
        orderItemRepo.save(item);
        orderRepo.save(cart);

        return ok(cart);
    }

    public CartResponse deleteItem(Long itemId, Authentication authentication) {
        var cart = getCart(authentication);
        var item = orderItemRepo.findById(itemId).orElseThrow(EntityNotFoundException::new);
        orderItemRepo.delete(item);

        return ok(cart);
    }

    public CartResponse clearCart(Authentication authentication) {
        var cart = getCart(authentication);
        var items = cart.getItems();
        cart.setItems(Collections.emptyList());
        orderItemRepo.deleteAll(items);

        return ok(cart);
    }
}
