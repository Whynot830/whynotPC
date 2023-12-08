package com.example.whynotpc.controllers;

import com.example.whynotpc.models.auth.AuthRequest;
import com.example.whynotpc.models.auth.ChangePasswordRequest;
import com.example.whynotpc.models.response.AuthResponse;
import com.example.whynotpc.services.AuthService;
import com.example.whynotpc.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return handleServiceCall(() -> authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        return handleServiceCall(() -> authService.login(request, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return handleServiceCall(() -> authService.refreshToken(request, response));
    }

    @PostMapping("/terminate-other")
    public ResponseEntity<AuthResponse> terminateOtherSessions(HttpServletRequest request) {
        return handleServiceCall(() -> authService.terminateOtherSessions(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@RequestBody ChangePasswordRequest request,
                                                       HttpServletRequest servletRequest
    ) {
        return handleServiceCall(() -> authService.changePassword(request, servletRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) {
        return handleServiceCall(() -> authService.logout(request));
    }
}
