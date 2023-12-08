package com.example.whynotpc.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse extends BasicResponse {
    @JsonProperty
    private final String message;
    public AuthResponse(int statusCode, String message) {
        super(statusCode);
        this.message = message;
    }
}
