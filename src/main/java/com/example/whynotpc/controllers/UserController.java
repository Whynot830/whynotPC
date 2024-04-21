package com.example.whynotpc.controllers;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

/**
 * Controller class for handling user-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param userDTO The DTO containing user information.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping()
    public ResponseEntity<? extends BasicResponse> create(@RequestBody UserDTO userDTO) {
        return getResponse(() -> userService.create(userDTO, null));
    }

    /**
     * Retrieves all users.
     *
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(userService::readAll);
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param id The ID of the user.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable Long id) {
        return getResponse(() -> userService.read(id));
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @param authentication The authentication object.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping("/current")
    public ResponseEntity<? extends BasicResponse> read(Authentication authentication) {
        return getResponse(() -> userService.read(authentication));
    }

    /**
     * Updates a user by ID.
     *
     * @param id      The ID of the user to update.
     * @param newUser The new user data.
     * @return ResponseEntity with a BasicResponse.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> update(
            @PathVariable Long id,
            @RequestBody UserDTO newUser
    ) {
        return getResponse(() -> userService.update(id, newUser));
    }

    /**
     * Updates the current authenticated user.
     *
     * @param authentication The authentication object.
     * @param newUser        The new user data.
     * @param file           The new image file for the user (optional).
     * @return ResponseEntity with a BasicResponse.
     */
    @PatchMapping("/current")
    public ResponseEntity<? extends BasicResponse> update(
            Authentication authentication,
            @RequestPart(required = false) UserDTO newUser,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> userService.update(authentication, newUser, file));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        return getResponse(() -> userService.delete(id));
    }
}
