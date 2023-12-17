package com.example.whynotpc.models.auth;

public record AuthRequest(
        String firstname,
        String lastname,
        String username,
        String email,
        String password
) {
}
