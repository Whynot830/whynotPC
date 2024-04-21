package com.example.whynotpc.models.auth;

/**
 * A record representing a change password request, containing the current password and the new password.
 */
public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
