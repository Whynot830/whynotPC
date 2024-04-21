package com.example.whynotpc.controllers;

import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

/**
 * Controller class for handling order-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    /**
     * Retrieves all orders.
     *
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(orderService::readAll);
    }

    /**
     * Retrieves a specific order by ID.
     *
     * @param id The ID of the order.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable Long id) {
        return getResponse(() -> orderService.read(id));
    }

    /**
     * Retrieves orders by user ID.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping(params = "userId")
    public ResponseEntity<? extends BasicResponse> readByUserId(@RequestParam Long userId) {
        return getResponse(() -> orderService.readByUserId(userId));
    }

    /**
     * Retrieves orders for the authenticated user.
     *
     * @param authentication The authentication object.
     * @param ignored        Ignored parameter to differentiate from other methods.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping(params = "current-user")
    public ResponseEntity<? extends BasicResponse> readByAuthentication(
            Authentication authentication,
            @RequestParam(name = "current-user") String ignored
    ) {
        return getResponse(() -> orderService.readByAuthentication(authentication));
    }

    /**
     * Deletes a specific order by ID.
     *
     * @param id The ID of the order to delete.
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        return getResponse(() -> orderService.delete(id));
    }
}
