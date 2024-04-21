package com.example.whynotpc.controllers;

import com.example.whynotpc.models.auth.ChangePasswordRequest;
import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

/**
 * Controller class for handling authentication-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * Handles registration endpoint.
     *
     * @param userDTO The user DTO.
     * @param file    The profile picture file (optional).
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/register")
    public ResponseEntity<? extends BasicResponse> register(
            @RequestPart UserDTO userDTO,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> authService.register(userDTO, file));
    }

    /**
     * Handles login endpoint.
     *
     * @param userDTO  The user DTO.
     * @param response The HTTP response.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/login")
    public ResponseEntity<? extends BasicResponse> login(
            @RequestBody UserDTO userDTO,
            HttpServletResponse response
    ) {
        return getResponse(() -> authService.login(userDTO, response));
    }

    /**
     * Handles token refresh endpoint.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<? extends BasicResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return getResponse(() -> authService.refreshToken(request, response));
    }

    /**
     * Handles terminating other sessions endpoint.
     *
     * @param request The HTTP request.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/terminate-other")
    public ResponseEntity<? extends BasicResponse> terminateOtherSessions(HttpServletRequest request) {
        return getResponse(() -> authService.terminateOtherSessions(request));
    }

    /**
     * Handles changing password endpoint.
     *
     * @param request       The change password request DTO.
     * @param servletRequest The HTTP servlet request.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/change-password")
    public ResponseEntity<? extends BasicResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest servletRequest
    ) {
        return getResponse(() -> authService.changePassword(request, servletRequest));
    }

    /**
     * Handles logout endpoint.
     *
     * @param request The HTTP request.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping("/logout")
    public ResponseEntity<? extends BasicResponse> logout(HttpServletRequest request) {
        return getResponse(() -> authService.logout(request));
    }
}
