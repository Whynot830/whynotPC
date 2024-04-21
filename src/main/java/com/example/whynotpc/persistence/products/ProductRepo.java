package com.example.whynotpc.persistence.products;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing product entities.
 */
@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    /**
     * Finds all products belonging to a specific category.
     *
     * @param category The category to which the products belong.
     * @return list of products belonging to the specified category.
     */
    List<Product> findAllByCategory(Category category);

    /**
     * Finds a page of products belonging to a specific category.
     *
     * @param category     The category to which the products belong.
     * @param pageRequest  Page request configuration for pagination.
     * @return page of products belonging to the specified category.
     */
    Page<Product> findPageByCategory(Category category, PageRequest pageRequest);
}
