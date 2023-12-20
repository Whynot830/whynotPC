package com.example.whynotpc.controllers;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<? extends BasicResponse> create(@RequestBody Category category) {
        return getResponse(() -> categoryService.create(category));
    }

    @PostMapping(params = "multiple")
    public ResponseEntity<? extends BasicResponse> create(
            @RequestBody List<Category> categories,
            @RequestParam(name = "multiple") String ignored
    ) {
        return getResponse(() -> categoryService.create(categories));
    }

    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(categoryService::readAll);
    }

    @GetMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable String name) {
        return getResponse(() -> categoryService.read(name));
    }

    @PatchMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> update(
            @PathVariable String name,
            @RequestBody Category newCategory
    ) {
        return getResponse(() -> categoryService.update(name, newCategory));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable String name) {
        return getResponse(() -> categoryService.delete(name));
    }

    @DeleteMapping
    public ResponseEntity<? extends BasicResponse> deleteAll() {
        return getResponse(categoryService::deleteAll);
    }
}
