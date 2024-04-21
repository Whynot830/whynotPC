package com.example.whynotpc.persistence.jwt;

import com.example.whynotpc.models.jwt.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing access token entities.
 */
public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {
    /**
     * Finds an access token by its token string.
     * @param token The token string to find.
     * @return Optional containing the access token if found, or empty if not found.
     */
    Optional<AccessToken> findByToken(String token);

    /**
     * Retrieves all access tokens for a user except the current one.
     * @param userId The ID of the user.
     * @param currToken The token string of the current access token to exclude.
     * @return list of access tokens belonging to the user except the current one.
     */
    @Query("SELECT a FROM AccessToken a WHERE a.user.id = :userId AND a.token != :currToken")
    List<AccessToken> getAllByUserIdExceptCurrent(Long userId, String currToken);
}
