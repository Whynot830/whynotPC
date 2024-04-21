package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.order.Order;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.UserResponse;
import com.example.whynotpc.models.users.Role;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.orders.OrderRepo;
import com.example.whynotpc.persistence.users.UserRepo;
import com.example.whynotpc.utils.NoAuthenticationException;
import com.example.whynotpc.utils.StrChecker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.UserResponse.created;
import static com.example.whynotpc.models.response.UserResponse.ok;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

/**
 * Service class for user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    /**
     * Retrieves a user from the provided authentication.
     *
     * @param authentication The authentication object.
     * @return user retrieved from the authentication.
     * @throws NoAuthenticationException If the authentication object is null.
     */
    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null)
            throw new NoAuthenticationException();
        return (User) authentication.getPrincipal();
    }

    /**
     * Creates a new user with the provided user DTO and profile picture file.
     *
     * @param userDTO The DTO containing user information.
     * @param file    The profile picture file.
     * @return response containing the created user.
     * @throws IllegalArgumentException If any of the user properties are null or blank.
     */
    public UserResponse create(UserDTO userDTO, MultipartFile file) {
        Stream<String> values = Stream.of(userDTO.firstname(), userDTO.lastname(), userDTO.username(),
                userDTO.email(), userDTO.password(), userDTO.role());
        if (values.anyMatch(StrChecker::isNullOrBlank))
            throw new IllegalArgumentException("Some of properties are null or blank");

        var user = User.builder()
                .firstname(userDTO.firstname())
                .lastname(userDTO.lastname())
                .username(userDTO.username())
                .email(userDTO.email())
                .password(passwordEncoder.encode(userDTO.password()))
                .role(Role.valueOf(userDTO.role()))
                .createdAt(LocalDateTime.now())
                .build();

        if (file != null) {
            imageService.create(file);
            String picName = Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_');
            user.setProfilePicName(picName);
        }
        user = userRepo.save(user);

        orderRepo.save(Order.builder()
                .status(CART)
                .total(BigDecimal.valueOf(0))
                .items(Collections.emptyList())
                .user(user)
                .build());

        return created(user);
    }

    /**
     * Retrieves all users.
     *
     * @return response containing all users.
     */
    public UserResponse readAll() {
        return ok(userRepo.findAll());
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return response containing the retrieved user.
     * @throws EntityNotFoundException If the user with the specified ID is not found.
     */
    public UserResponse read(Long id) {
        var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        return ok(user);
    }

    /**
     * Retrieves the authenticated user.
     *
     * @param authentication The authentication object.
     * @return response containing the authenticated user.
     */
    public UserResponse read(Authentication authentication) {
        var user = getUserFromAuthentication(authentication);

        return ok(user);
    }

    /**
     * Updates a user's information.
     *
     * @param id   The ID of the user to update.
     * @param user The updated user information.
     * @return updated user.
     * @throws EntityNotFoundException If the user with the specified ID is not found.
     */
    public User update(Long id, User user) {
        if (!userRepo.existsById(id))
            throw new EntityNotFoundException("User with id " + id + " not found");

        user.setId(id);
        return userRepo.save(user);
    }

    /**
     * Updates a user's information using a DTO.
     *
     * @param id     The ID of the user to update.
     * @param newUser The DTO containing the updated user information.
     * @return response containing the updated user.
     * @throws EntityNotFoundException If the user with the specified ID is not found.
     */
    public UserResponse update(Long id, UserDTO newUser) {
        var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!isNullOrBlank(newUser.firstname())) user.setFirstname(newUser.firstname());
        if (!isNullOrBlank(newUser.lastname())) user.setLastname(newUser.lastname());
        if (!isNullOrBlank(newUser.email())) user.setEmail(newUser.email());
        if (!isNullOrBlank(newUser.password())) user.setPassword(passwordEncoder.encode(newUser.password()));
        if (!isNullOrBlank(newUser.role())) user.setRole(Role.valueOf(newUser.role()));
        user = userRepo.save(user);

        return ok(user);
    }

    /**
     * Updates a user's information and profile picture.
     *
     * @param authentication The authentication object.
     * @param newUser        The DTO containing the updated user information.
     * @param file           The new profile picture file.
     * @return response containing the updated user.
     */
    public UserResponse update(Authentication authentication, UserDTO newUser, MultipartFile file) {
        var user = getUserFromAuthentication(authentication);
        if (newUser != null) {
            if (!isNullOrBlank(newUser.firstname())) user.setFirstname(newUser.firstname());
            if (!isNullOrBlank(newUser.lastname())) user.setLastname(newUser.lastname());
            if (!isNullOrBlank(newUser.email())) user.setEmail(newUser.email());
        }

        if (file != null) {
            var presentImage = imageService.read(user.getProfilePicName());

            if (presentImage == null) imageService.create(file);
            else imageService.update(file, presentImage.getName());

            user.setProfilePicName(Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '_'));
        }

        user = update(user.getId(), user);
        return ok(user);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete.
     * @return response indicating the success of the operation.
     * @throws EntityNotFoundException If the user with the specified ID is not found.
     */
    public BasicResponse delete(Long id) {
        var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        userRepo.delete(user);

        return noContent();
    }

    /**
     * Finds a user by username.
     *
     * @param username The username of the user to find.
     * @return Optional containing the user, or empty if not found.
     */
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * Finds a user by email.
     *
     * @param email The email address of the user to find.
     * @return Optional containing the user, or empty if not found.
     */
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
