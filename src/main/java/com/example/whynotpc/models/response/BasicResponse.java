package com.example.whynotpc.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a basic response for API operations.
 */
@Data
@AllArgsConstructor
public class BasicResponse {
    /**
     * The HTTP status code of the response.
     */
    @JsonIgnore
    private final int statusCode;

    /**
     * Factory method to create a BasicResponse with HTTP status code 204 (No Content).
     * @return BasicResponse with status code 204.
     */
    public static BasicResponse noContent() {
        return new BasicResponse(204);
    }
}
