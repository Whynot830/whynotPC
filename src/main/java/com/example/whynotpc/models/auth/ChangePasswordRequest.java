package com.example.whynotpc.models.auth;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
