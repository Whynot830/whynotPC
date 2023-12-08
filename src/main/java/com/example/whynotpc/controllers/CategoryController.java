package com.example.whynotpc.controllers;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.response.CategoryResponse;
import com.example.whynotpc.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody Category category) {
        return handleServiceCall(() -> categoryService.create(category));
    }

    @GetMapping
    public ResponseEntity<CategoryResponse> readAll() {
        return handleServiceCall(categoryService::readAll);
    }

    @GetMapping("/{name}")
    public ResponseEntity<CategoryResponse> read(@PathVariable String name) {
        return handleServiceCall(() -> categoryService.read(name));
    }

    @PatchMapping("/{name}")
    public ResponseEntity<CategoryResponse> update(@PathVariable String name, @RequestBody Category newCategory) {
        return handleServiceCall(() -> categoryService.update(name, newCategory));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<CategoryResponse> delete(@PathVariable String name) {
        return handleServiceCall(() -> categoryService.delete(name));
    }

    @DeleteMapping
    public ResponseEntity<CategoryResponse> delete() {
        return handleServiceCall(categoryService::deleteAll);
    }
}
