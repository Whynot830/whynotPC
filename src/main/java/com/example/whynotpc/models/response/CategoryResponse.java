package com.example.whynotpc.models.response;

import com.example.whynotpc.models.product.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class CategoryResponse extends BasicResponse {
    @JsonProperty
    private final List<Category> categories;

    public CategoryResponse(int statusCode, List<Category> categories) {
        super(statusCode);
        this.categories = categories;
    }

    public CategoryResponse(int statusCode, Category category) {
        super(statusCode);
        this.categories = Collections.singletonList(category);
    }

    public static CategoryResponse ok(List<Category> categories) {
        return new CategoryResponse(200, categories);
    }

    public static CategoryResponse ok(Category category) {
        return new CategoryResponse(200, category);
    }

    public static CategoryResponse created(Category category) {
        return new CategoryResponse(201, category);
    }

    public static CategoryResponse created(List<Category> categories) {
        return new CategoryResponse(201, categories);
    }
}