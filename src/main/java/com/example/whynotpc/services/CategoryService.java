package com.example.whynotpc.services;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.response.CategoryResponse;
import com.example.whynotpc.persistence.products.CategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.whynotpc.utils.JPACallHandler.handleCall;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;

    public CategoryResponse create(Category category) {
        var response = handleCall(() -> {
            if (isNullOrBlank(category.getName()))
                throw new IllegalArgumentException("Category name is null or blank");
            return categoryRepo.save(category);
        });
        return switch (response.statusCode()) {
            case 200 -> new CategoryResponse(201, response.result());
            case 400 -> new CategoryResponse(400);
            case 409 -> new CategoryResponse(409);
            default -> new CategoryResponse(500);
        };
    }

    public CategoryResponse readAll() {
        return new CategoryResponse(200, categoryRepo.findAll());
    }

    public CategoryResponse read(String name) {
        return categoryRepo.findByName(name)
                .map(category -> new CategoryResponse(200, category))
                .orElse(new CategoryResponse(404));
    }

    public CategoryResponse update(String name, Category newCategory) {
        var response = handleCall(() -> {
            var category = categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
            if (isNullOrBlank(newCategory.getName()))
                throw new IllegalArgumentException("Category name is null or blank");
            category.setName(newCategory.getName());
            return categoryRepo.save(category);
        });
        return switch (response.statusCode()) {
            case 200 -> new CategoryResponse(200, response.result());
            case 400 -> new CategoryResponse(400);
            case 404 -> new CategoryResponse(404);
            case 409 -> new CategoryResponse(409);
            default -> new CategoryResponse(500);
        };
    }

    public CategoryResponse delete(String name) {
        var response = handleCall(() -> {
            var category = categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
            categoryRepo.delete(category);
            return true;
        });
        return switch (response.statusCode()) {
            case 200 -> new CategoryResponse(204);
            case 404 -> new CategoryResponse(404);
            default -> new CategoryResponse(500);
        };
    }

    public CategoryResponse deleteAll() {
        categoryRepo.deleteAll();
        return new CategoryResponse(204);
    }
}
