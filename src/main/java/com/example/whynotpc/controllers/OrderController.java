package com.example.whynotpc.controllers;

import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(orderService::readAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable Long id) {
        return getResponse(() -> orderService.read(id));
    }

    @GetMapping(params = "userId")
    public ResponseEntity<? extends BasicResponse> readByUserId(@RequestParam Long userId) {
        return getResponse(() -> orderService.readByUserId(userId));
    }

    @GetMapping(params = "current-user")
    public ResponseEntity<? extends BasicResponse> readByAuthentication(
            Authentication authentication,
            @RequestParam(name = "current-user") String ignored
    ) {
        return getResponse(() -> orderService.readByAuthentication(authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        return getResponse(() -> orderService.delete(id));
    }
}
