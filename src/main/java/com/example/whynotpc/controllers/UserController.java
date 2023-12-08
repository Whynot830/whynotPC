package com.example.whynotpc.controllers;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.response.UserResponse;
import com.example.whynotpc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponse> create(@RequestBody UserDTO userDTO) {
        return handleServiceCall(() -> userService.create(userDTO));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> readAll() {
        return ResponseEntity.ok(userService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> read(@PathVariable Integer id) {
        return handleServiceCall(() -> userService.read(id));
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponse> read(Authentication authentication) {
        return handleServiceCall(() -> userService.read(authentication));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody UserDTO newUser) {
        return handleServiceCall(() -> userService.update(id, newUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable Integer id) {
        return handleServiceCall(() -> userService.delete(id));
    }
}
