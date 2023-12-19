package com.example.whynotpc.controllers;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<? extends BasicResponse> create(@RequestBody UserDTO userDTO) {
        return getResponse(() -> userService.create(userDTO));
    }

    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(userService::readAll);
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable Long id) {
        return getResponse(() -> userService.read(id));
    }

    @GetMapping("/current")
    public ResponseEntity<? extends BasicResponse> read(Authentication authentication) {
        return getResponse(() -> userService.read(authentication));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> update(@PathVariable Long id, @RequestBody UserDTO newUser) {
        return getResponse(() -> userService.update(id, newUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        return getResponse(() -> userService.delete(id));
    }
}
