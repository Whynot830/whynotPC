package com.example.whynotpc.persistence.jwt;

import com.example.whynotpc.models.jwt.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing refresh token entities.
 */
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    /**
     * Finds a refresh token by its token string.
     * @param token The token string to find.
     * @return Optional containing the refresh token if found, or empty if not found.
     */
    Optional<RefreshToken> findByToken(String token);
}
