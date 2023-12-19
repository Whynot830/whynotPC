package com.example.whynotpc.models.response;

public class AuthResponse extends BasicResponse {
    public AuthResponse(int statusCode) {
        super(statusCode);
    }

    public static AuthResponse ok() {
        return new AuthResponse(200);
    }
}
