package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.users.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class UserResponse extends BasicResponse {
    @JsonProperty
    private final List<UserDTO> users;

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstname(), user.getLastname(),
                user.getUsername(), user.getEmail(), user.getRole().name(), user.getCreatedAt(), user.getProfilePicName());
    }

    public UserResponse(int statusCode, List<User> users) {
        super(statusCode);
        this.users = users.stream().map(this::toDTO).toList();
    }

    public UserResponse(int statusCode, User user) {
        super(statusCode);
        this.users = Collections.singletonList(toDTO(user));
    }

    public static UserResponse ok(List<User> users) {
        return new UserResponse(200, users);
    }

    public static UserResponse ok(User user) {
        return new UserResponse(200, user);
    }

    public static UserResponse created(User user) {
        return new UserResponse(201, user);
    }
}
