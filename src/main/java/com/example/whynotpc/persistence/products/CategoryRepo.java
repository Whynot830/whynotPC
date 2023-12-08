package com.example.whynotpc.persistence.products;

import com.example.whynotpc.models.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
//    long deleteByName(String name);
}
