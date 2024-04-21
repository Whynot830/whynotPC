package com.example.whynotpc.config;

import com.example.whynotpc.persistence.users.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for setting up application beans related to security.
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final UserRepo userRepo;

    /**
     * Provides a bean for encoding passwords using BCryptPasswordEncoder.
     *
     * @return BCryptPasswordEncoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a bean for loading user-specific data.
     *
     * @return UserDetailsService bean.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return usernameOrEmail -> userRepo.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepo.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    /**
     * Provides a bean for managing authentication.
     *
     * @param config AuthenticationConfiguration object.
     * @return AuthenticationManager bean.
     * @throws Exception If an error occurs during authentication manager setup.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides a bean for authentication.
     *
     * @return AuthenticationProvider bean.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
