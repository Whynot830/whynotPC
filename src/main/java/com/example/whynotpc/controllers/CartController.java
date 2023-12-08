package com.example.whynotpc.controllers;

import com.example.whynotpc.models.response.CartResponse;
import com.example.whynotpc.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> read(Authentication authentication) {
        return handleServiceCall(() -> cartService.read(authentication));
    }

    @PostMapping("/confirm")
    public ResponseEntity<CartResponse> checkOut(Authentication authentication) {
        return handleServiceCall(() -> cartService.checkOut(authentication));
    }

    @PostMapping(value = "/items", params = "productId")
    public ResponseEntity<CartResponse> addItem(Authentication authentication, @RequestParam Integer productId) {
        return handleServiceCall(() -> cartService.addItem(productId, authentication));
    }

    @PatchMapping(value = "/items/{itemId}", params = "quantity")
    public ResponseEntity<CartResponse> updateQuantity(Authentication authentication, @PathVariable Integer itemId,
                                                       @RequestParam Integer quantity
    ) {
        return handleServiceCall(() -> cartService.updateItemQuantity(itemId, quantity, authentication));
    }

    @DeleteMapping(value = "/items/{itemId}")
    public ResponseEntity<CartResponse> deleteItem(Authentication authentication, @PathVariable Integer itemId) {
        return handleServiceCall(() -> cartService.deleteItem(itemId, authentication));
    }

    @DeleteMapping("/items")
    public ResponseEntity<CartResponse> clear(Authentication authentication) {
        return handleServiceCall(() -> cartService.clearCart(authentication));
    }
}
