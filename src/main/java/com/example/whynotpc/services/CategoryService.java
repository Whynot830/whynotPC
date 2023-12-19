package com.example.whynotpc.services;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.CategoryResponse;
import com.example.whynotpc.persistence.products.CategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.CategoryResponse.created;
import static com.example.whynotpc.models.response.CategoryResponse.ok;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;

    public CategoryResponse create(Category category) {
        if (isNullOrBlank(category.getName()))
            throw new IllegalArgumentException("Category name is null or blank");
        category = categoryRepo.save(category);
        return created(category);
    }

    public CategoryResponse readAll() {
        return ok(categoryRepo.findAll());
    }

    public CategoryResponse read(String name) {
        return categoryRepo.findByName(name)
                .map(CategoryResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    public CategoryResponse update(String name, Category newCategory) {
        var category = categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        if (isNullOrBlank(newCategory.getName()))
            throw new IllegalArgumentException("Category name is null or blank");
        category.setName(newCategory.getName());

        return ok(categoryRepo.save(category));
    }

    public BasicResponse delete(String name) {
        var category = categoryRepo.findByName(name).orElseThrow(EntityNotFoundException::new);
        categoryRepo.delete(category);

        return noContent();
    }

    public BasicResponse deleteAll() {
        categoryRepo.deleteAll();
        return noContent();
    }
}
