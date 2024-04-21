package com.example.whynotpc.controllers;

import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

/**
 * Controller class for handling cart-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    /**
     * Retrieves the user's cart.
     *
     * @param authentication The authentication object.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping
    public ResponseEntity<? extends BasicResponse> read(Authentication authentication) {
        return getResponse(() -> cartService.read(authentication));
    }

    /**
     * Checks out the user's cart.
     *
     * @param authentication The authentication object.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/checkout")
    public ResponseEntity<? extends BasicResponse> checkout(Authentication authentication) {
        return getResponse(() -> cartService.checkout(authentication));
    }

    /**
     * Adds an item to the user's cart.
     *
     * @param authentication The authentication object.
     * @param productId      The ID of the product to add.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping(value = "/items", params = "productId")
    public ResponseEntity<? extends BasicResponse> addItem(
            Authentication authentication,
            @RequestParam Long productId
    ) {
        return getResponse(() -> cartService.addItem(productId, authentication));
    }

    /**
     * Updates the quantity of an item in the user's cart.
     *
     * @param authentication The authentication object.
     * @param itemId         The ID of the item to update.
     * @param quantity       The new quantity.
     * @return ResponseEntity with a BasicResponse.
     */
    @PatchMapping(value = "/items/{itemId}", params = "quantity")
    public ResponseEntity<? extends BasicResponse> updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestParam Integer quantity
    ) {
        return getResponse(() -> cartService.updateItemQuantity(itemId, quantity, authentication));
    }

    /**
     * Deletes an item from the user's cart.
     *
     * @param authentication The authentication object.
     * @param itemId         The ID of the item to delete.
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping(value = "/items/{itemId}")
    public ResponseEntity<? extends BasicResponse> deleteItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        return getResponse(() -> cartService.deleteItem(itemId, authentication));
    }

    /**
     * Clears the user's cart.
     *
     * @param authentication The authentication object.
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping("/items")
    public ResponseEntity<? extends BasicResponse> clear(Authentication authentication) {
        return getResponse(() -> cartService.clearCart(authentication));
    }
}
