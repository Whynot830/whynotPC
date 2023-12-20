package com.example.whynotpc.controllers;

import com.example.whynotpc.models.auth.AuthRequest;
import com.example.whynotpc.models.auth.ChangePasswordRequest;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<? extends BasicResponse> register(@RequestBody AuthRequest request) {
        return getResponse(() -> authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<? extends BasicResponse> login(
            @RequestBody AuthRequest request,
            HttpServletResponse response
    ) {
        return getResponse(() -> authService.login(request, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<? extends BasicResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return getResponse(() -> authService.refreshToken(request, response));
    }

    @PostMapping("/terminate-other")
    public ResponseEntity<? extends BasicResponse> terminateOtherSessions(HttpServletRequest request) {
        return getResponse(() -> authService.terminateOtherSessions(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<? extends BasicResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest servletRequest
    ) {
        return getResponse(() -> authService.changePassword(request, servletRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<? extends BasicResponse> logout(HttpServletRequest request) {
        return getResponse(() -> authService.logout(request));
    }
}
