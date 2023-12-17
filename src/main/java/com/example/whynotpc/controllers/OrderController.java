package com.example.whynotpc.controllers;

import com.example.whynotpc.models.dto.OrderDTO;
import com.example.whynotpc.models.response.OrderResponse;
import com.example.whynotpc.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<OrderResponse> readAll() {
        return handleServiceCall(orderService::readAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> read(@PathVariable Integer id) {
        return handleServiceCall(() -> orderService.read(id));
    }

    @GetMapping(params = "userId")
    public ResponseEntity<OrderResponse> readByUserId(@RequestParam Integer userId) {
        return handleServiceCall(() -> orderService.readByUserId(userId));
    }

    @GetMapping(value = "/current-user")
    public ResponseEntity<OrderResponse> readByAuthentication(Authentication authentication) {
        return handleServiceCall(() -> orderService.readByAuthentication(authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> delete(@PathVariable Integer id) {
        return handleServiceCall(() -> orderService.delete(id));
    }
}
