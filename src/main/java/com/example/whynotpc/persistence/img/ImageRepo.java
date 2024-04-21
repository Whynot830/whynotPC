package com.example.whynotpc.persistence.img;

import com.example.whynotpc.models.img.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing image entities.
 */
public interface ImageRepo extends JpaRepository<Image, Long> {
    /**
     * Finds an image by its name.
     * @param name The name of the image to find.
     * @return Optional containing the image if found, or empty if not found.
     */
    Optional<Image> findByName(String name);

    /**
     * Checks if an image with the given name exists.
     * @param name The name of the image to check.
     * @return true if an image with the given name exists, false otherwise.
     */
    boolean existsByName(String name);
}
