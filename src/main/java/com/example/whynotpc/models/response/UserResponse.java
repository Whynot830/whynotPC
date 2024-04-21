package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.users.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Represents a response for user-related operations.
 * Extends BasicResponse class.
 */
public class UserResponse extends BasicResponse {
    /**
     * The list of user DTOs included in the response.
     */
    @JsonProperty
    private final List<UserDTO> users;

    /**
     * Converts a User object to its corresponding DTO representation.
     * @param user The User object to convert.
     * @return UserDTO representation of the User object.
     */
    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstname(), user.getLastname(),
                user.getUsername(), user.getEmail(), user.getRole().name(), user.getCreatedAt(), user.getProfilePicName());
    }

    /**
     * Constructs a new UserResponse with the given status code and list of users.
     * @param statusCode The HTTP status code of the response.
     * @param users The list of users to include in the response.
     */
    public UserResponse(int statusCode, List<User> users) {
        super(statusCode);
        this.users = users.stream().map(this::toDTO).toList();
    }

    /**
     * Constructs a new UserResponse with the given status code and user.
     * @param statusCode The HTTP status code of the response.
     * @param user The user to include in the response.
     */
    public UserResponse(int statusCode, User user) {
        super(statusCode);
        this.users = Collections.singletonList(toDTO(user));
    }

    /**
     * Factory method to create a UserResponse with HTTP status code 200 (OK) and the given list of users.
     * @param users The list of users to include in the response.
     * @return UserResponse with status code 200 and the given list of users.
     */
    public static UserResponse ok(List<User> users) {
        return new UserResponse(200, users);
    }

    /**
     * Factory method to create a UserResponse with HTTP status code 200 (OK) and the given user.
     * @param user The user to include in the response.
     * @return UserResponse with status code 200 and the given user.
     */
    public static UserResponse ok(User user) {
        return new UserResponse(200, user);
    }

    /**
     * Factory method to create a UserResponse with HTTP status code 201 (Created) and the given user.
     * @param user The user to include in the response.
     * @return UserResponse with status code 201 and the given user.
     */
    public static UserResponse created(User user) {
        return new UserResponse(201, user);
    }
}
