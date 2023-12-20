package com.example.whynotpc.controllers;

import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<? extends BasicResponse> read(Authentication authentication) {
        return getResponse(() -> cartService.read(authentication));
    }

    @PostMapping("/checkout")
    public ResponseEntity<? extends BasicResponse> checkOut(Authentication authentication) {
        return getResponse(() -> cartService.checkOut(authentication));
    }

    @PostMapping(value = "/items", params = "productId")
    public ResponseEntity<? extends BasicResponse> addItem(
            Authentication authentication,
            @RequestParam Long productId
    ) {
        return getResponse(() -> cartService.addItem(productId, authentication));
    }

    @PatchMapping(value = "/items/{itemId}", params = "quantity")
    public ResponseEntity<? extends BasicResponse> updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestParam Integer quantity
    ) {
        return getResponse(() -> cartService.updateItemQuantity(itemId, quantity, authentication));
    }

    @DeleteMapping(value = "/items/{itemId}")
    public ResponseEntity<? extends BasicResponse> deleteItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        return getResponse(() -> cartService.deleteItem(itemId, authentication));
    }

    @DeleteMapping("/items")
    public ResponseEntity<? extends BasicResponse> clear(Authentication authentication) {
        return getResponse(() -> cartService.clearCart(authentication));
    }
}
