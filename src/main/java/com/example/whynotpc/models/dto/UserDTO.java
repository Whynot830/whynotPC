package com.example.whynotpc.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record UserDTO(Integer id, String firstname, String lastname, String username, String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password, String role,
        @JsonFormat(pattern = "MM-dd-yyyy hh:mm a z",
                timezone = "Europe/Moscow")
        LocalDateTime createdAt) {
    public UserDTO(String firstname, String lastname, String username, String email,
                   String password, String role, LocalDateTime createdAt
    ) {
        this(null, firstname, lastname, username, email, password, role, createdAt);
    }

    public UserDTO(Integer id, String firstname, String lastname, String username,
                   String email, String role, LocalDateTime createdAt
    ) {
        this(id, firstname, lastname, username, email, null, role, createdAt);
    }
}
