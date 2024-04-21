package com.example.whynotpc.models.response;

/**
 * Represents a response for authentication-related operations.
 * Extends BasicResponse class.
 */
public class AuthResponse extends BasicResponse {
    /**
     * Constructs a new AuthResponse with the given status code.
     * @param statusCode The HTTP status code of the response.
     */
    public AuthResponse(int statusCode) {
        super(statusCode);
    }

    /**
     * Factory method to create an AuthResponse with HTTP status code 200 (OK).
     * @return AuthResponse with status code 200.
     */
    public static AuthResponse ok() {
        return new AuthResponse(200);
    }
}
