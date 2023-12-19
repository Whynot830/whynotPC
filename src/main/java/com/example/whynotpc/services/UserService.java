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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.whynotpc.models.order.OrderStatus.CART;
import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.UserResponse.created;
import static com.example.whynotpc.models.response.UserResponse.ok;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserDTO userDTO) {
        Stream<String> values = Stream.of(userDTO.firstname(), userDTO.lastname(), userDTO.username(),
                userDTO.email(), userDTO.password(), userDTO.role());
        if (values.anyMatch(StrChecker::isNullOrBlank))
            throw new IllegalArgumentException("Some of properties are null or blank");

        var user = userRepo.save(User.builder()
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
                .total(BigDecimal.valueOf(0))
                .items(Collections.emptyList())
                .user(user)
                .build());

        return created(user);
    }

    public UserResponse readAll() {
        return ok(userRepo.findAll());
    }

    public UserResponse read(Long id) {
        var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        return ok(user);
    }

    public UserResponse read(Authentication authentication) {
        if (authentication == null)
            throw new NoAuthenticationException();
        var user = (User) authentication.getPrincipal();

        return ok(user);
    }

    public void update(Long id, User user) {
        if (!userRepo.existsById(id))
            return;
        user.setId(id);
        userRepo.save(user);
    }

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

    public BasicResponse delete(Long id) {
        var user = userRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        userRepo.delete(user);

        return noContent();
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
