package com.example.whynotpc.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * A data transfer object (DTO) representing a user.
 */
public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String username,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String role,
        @JsonFormat(pattern = "MM-dd-yyyy hh:mm a z",
                timezone = "Europe/Moscow")
        LocalDateTime createdAt,
        String profilePicName
) {
    public UserDTO(String firstname, String lastname, String username, String email,
                   String password, String role, LocalDateTime createdAt
    ) {
        this(null, firstname, lastname, username, email, password, role, createdAt, null);
    }

    public UserDTO(Long id, String firstname, String lastname, String username,
                   String email, String role, LocalDateTime createdAt, String getProfilePicName
    ) {
        this(id, firstname, lastname, username, email, null, role, createdAt, getProfilePicName);
    }
}
