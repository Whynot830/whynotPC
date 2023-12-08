package com.example.whynotpc.utils;

import com.example.whynotpc.models.dto.UserDTO;
import com.example.whynotpc.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StartupInitializer implements CommandLineRunner {
    private final UserService userService;

    @Override
    public void run(String... args) {
        userService.create(new UserDTO("why", "not", "admin",
                "aminfury@mail.ru", "admin", "ADMIN", LocalDateTime.now()));
    }
}
