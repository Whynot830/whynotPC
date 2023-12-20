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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<? extends BasicResponse> register(
            @RequestPart UserDTO userDTO,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> authService.register(userDTO, file));
    }

    @PostMapping("/login")
    public ResponseEntity<? extends BasicResponse> login(
            @RequestBody UserDTO userDTO,
            HttpServletResponse response
    ) {
        return getResponse(() -> authService.login(userDTO, response));
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
