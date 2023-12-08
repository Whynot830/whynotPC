package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.models.order.Order;
import com.example.whynotpc.models.response.UserResponse;
import com.example.whynotpc.models.users.Role;
import com.example.whynotpc.models.users.User;
import com.example.whynotpc.persistence.orders.OrderRepo;
import com.example.whynotpc.persistence.users.UserRepo;
import com.example.whynotpc.utils.StrChecker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.utils.JPACallHandler.handleCall;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final PasswordEncoder passwordEncoder;

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstname(), user.getLastname(),
                user.getUsername(), user.getEmail(), user.getRole().name(), user.getCreatedAt());
    }

    public UserResponse create(UserDTO userDTO) {
        var response = handleCall(() -> {
            Stream<String> values = Stream.of(userDTO.firstname(), userDTO.lastname(), userDTO.username(),
                    userDTO.email(), userDTO.password(), userDTO.role());
            if (values.anyMatch(StrChecker::isNullOrBlank))
                throw new IllegalArgumentException("Some of properties are null or blank");

            var savedUser = userRepo.save(User.builder()
                    .firstname(userDTO.firstname())
                    .lastname(userDTO.lastname())
                    .username(userDTO.username())
                    .email(userDTO.email())
                    .password(passwordEncoder.encode(userDTO.password()))
                    .role(Role.valueOf(userDTO.role()))
                    .createdAt(LocalDateTime.now())
                    .build());

            orderRepo.save(Order.builder()
                    .status(CART)
                    .total(0.0f)
                    .items(Collections.emptyList())
                    .user(savedUser)
                    .build());
            return savedUser;
        });
        return switch (response.statusCode()) {
            case 200 -> new UserResponse(201, toDTO(response.result()));
            case 400 -> new UserResponse(400);
            case 409 -> new UserResponse(409);
            default -> new UserResponse(500);
        };
    }

    public List<UserDTO> readAll() {
        return userRepo.findAll().stream().map(this::toDTO).toList();
    }

    public UserResponse read(Integer id) {
        var response = handleCall(() -> userRepo.findById(id).orElseThrow(EntityNotFoundException::new));
        return switch (response.statusCode()) {
            case 200 -> new UserResponse(200, toDTO(response.result()));
            case 404 -> new UserResponse(404);
            default -> new UserResponse(500);
        };
    }

    public UserResponse read(Authentication authentication) {
        if (authentication == null)
            return new UserResponse(401);

        var user = (User) authentication.getPrincipal();
        return read(user.getId());
    }

    public void update(Integer id, User user) {
        if (!userRepo.existsById(id))
            return;
        user.setId(id);
        userRepo.save(user);
    }

    public UserResponse update(Integer id, UserDTO newUser) {
        var response = handleCall(() -> {
            var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);

            if (!isNullOrBlank(newUser.firstname())) user.setFirstname(newUser.firstname());
            if (!isNullOrBlank(newUser.lastname())) user.setLastname(newUser.lastname());
            if (!isNullOrBlank(newUser.email())) user.setEmail(newUser.email());
            if (!isNullOrBlank(newUser.password())) user.setPassword(passwordEncoder.encode(newUser.password()));
            if (!isNullOrBlank(newUser.role())) user.setRole(Role.valueOf(newUser.role()));

            return userRepo.save(user);
        });
        return switch (response.statusCode()) {
            case 200 -> new UserResponse(200, toDTO(response.result()));
            case 400 -> new UserResponse(400);
            case 409 -> new UserResponse(409);
            default -> new UserResponse(500);
        };
    }

    public UserResponse delete(Integer id) {
        return userRepo.findById(id)
                .map(user -> {
                    userRepo.delete(user);
                    return new UserResponse(204);
                })
                .orElse(new UserResponse(404));
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
