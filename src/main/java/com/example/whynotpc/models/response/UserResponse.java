package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse extends BasicResponse {
    @JsonProperty
    private final UserDTO user;

    public UserResponse(int statusCode, UserDTO user) {
        super(statusCode);
        this.user = user;
    }

    public UserResponse(int code) {
        this(code, null);
    }
}
