package com.example.whynotpc.models.response;

import com.example.whynotpc.models.product.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
/**
 * Represents a response for category-related operations.
 * Extends BasicResponse class.
 */
public class CategoryResponse extends BasicResponse {
    /**
     * The list of categories included in the response.
     */
    @JsonProperty
    private final List<Category> categories;

    /**
     * Constructs a new CategoryResponse with the given status code and list of categories.
     * @param statusCode The HTTP status code of the response.
     * @param categories The list of categories to include in the response.
     */
    public CategoryResponse(int statusCode, List<Category> categories) {
        super(statusCode);
        this.categories = categories;
    }

    /**
     * Constructs a new CategoryResponse with the given status code and single category.
     * @param statusCode The HTTP status code of the response.
     * @param category The category to include in the response.
     */
    public CategoryResponse(int statusCode, Category category) {
        super(statusCode);
        this.categories = Collections.singletonList(category);
    }

    /**
     * Factory method to create a CategoryResponse with HTTP status code 200 (OK) and the given list of categories.
     * @param categories The list of categories to include in the response.
     * @return CategoryResponse with status code 200 and the given list of categories.
     */
    public static CategoryResponse ok(List<Category> categories) {
        return new CategoryResponse(200, categories);
    }

    /**
     * Factory method to create a CategoryResponse with HTTP status code 200 (OK) and the given category.
     * @param category The category to include in the response.
     * @return CategoryResponse with status code 200 and the given category.
     */
    public static CategoryResponse ok(Category category) {
        return new CategoryResponse(200, category);
    }

    /**
     * Factory method to create a CategoryResponse with HTTP status code 201 (Created) and the given category.
     * @param category The category to include in the response.
     * @return CategoryResponse with status code 201 and the given category.
     */
    public static CategoryResponse created(Category category) {
        return new CategoryResponse(201, category);
    }

    /**
     * Factory method to create a CategoryResponse with HTTP status code 201 (Created) and the given list of categories.
     * @param categories The list of categories to include in the response.
     * @return CategoryResponse with status code 201 and the given list of categories.
     */
    public static CategoryResponse created(List<Category> categories) {
        return new CategoryResponse(201, categories);
    }
}