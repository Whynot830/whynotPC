package com.example.whynotpc.persistence.users;

import com.example.whynotpc.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository interface for managing user entities.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    /**
     * Finds a user by username.
     *
     * @param username The username of the user to find.
     * @return Optional containing the user with the specified username, or empty if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email address.
     *
     * @param email The email address of the user to find.
     * @return Optional containing the user with the specified email address, or empty if not found.
     */
    Optional<User> findByEmail(String email);
}
