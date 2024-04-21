package com.example.whynotpc.services;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.CategoryResponse;
import com.example.whynotpc.persistence.products.CategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.CategoryResponse.created;
import static com.example.whynotpc.models.response.CategoryResponse.ok;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

/**
 * Service class handling operations related to categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;

    /**
     * Saves a single category.
     *
     * @param category The category to save
     * @return saved category
     * @throws IllegalArgumentException If the category name is null or blank
     */
    private Category save(Category category) {
        if (isNullOrBlank(category.getName()))
            throw new IllegalArgumentException("Category name is null or blank");
        return categoryRepo.save(category);
    }

    /**
     * Creates a single category.
     *
     * @param category The category to create
     * @return response containing the created category
     */
    public CategoryResponse create(Category category) {
        var savedCategory = save(category);
        return created(savedCategory);
    }

    /**
     * Creates multiple categories in a transaction.
     *
     * @param categories The list of categories to create
     * @return response containing the created categories
     */
    @Transactional
    public CategoryResponse create(List<Category> categories) {
        List<Category> savedCategories = new ArrayList<>();
        Category savedCategory;
        for (var category : categories) {
            savedCategory = save(category);
            savedCategories.add(savedCategory);
        }
        return created(savedCategories);
    }

    /**
     * Retrieves all categories.
     *
     * @return response containing all categories
     */
    public CategoryResponse readAll() {
        return ok(categoryRepo.findAll());
    }

    /**
     * Retrieves a category by its name.
     *
     * @param name The name of the category to retrieve
     * @return response containing the retrieved category
     * @throws EntityNotFoundException If the category with the given name does not exist
     */
    public CategoryResponse read(String name) {
        return categoryRepo.findByName(name)
                .map(CategoryResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Updates a category by its name.
     *
     * @param name       The name of the category to update
     * @param newCategory The new category data
     * @return response containing the updated category
     * @throws EntityNotFoundException If the category with the given name does not exist
     * @throws IllegalArgumentException If the new category name is null or blank
     */
    public CategoryResponse update(String name, Category newCategory) {
        var category = categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        if (isNullOrBlank(newCategory.getName()))
            throw new IllegalArgumentException("Category name is null or blank");
        category.setName(newCategory.getName());

        return ok(categoryRepo.save(category));
    }

    /**
     * Deletes a category by its name.
     *
     * @param name The name of the category to delete
     * @return response indicating success
     * @throws EntityNotFoundException If the category with the given name does not exist
     */
    public BasicResponse delete(String name) {
        var category = categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        categoryRepo.delete(category);

        return noContent();
    }

    /**
     * Deletes all categories.
     *
     * @return response indicating success
     */
    public BasicResponse deleteAll() {
        categoryRepo.deleteAll();
        return noContent();
    }
}
