package com.example.whynotpc.persistence.products;

import com.example.whynotpc.models.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing category entities.
 */
public interface CategoryRepo extends JpaRepository<Category, Long> {
    /**
     * Finds a category by its name.
     *
     * @param name The name of the category.
     * @return Optional containing the category if found, or an empty optional if not found.
     */
    Optional<Category> findByName(String name);
}
