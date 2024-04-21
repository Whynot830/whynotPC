package com.example.whynotpc.controllers;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

/**
 * Controller class for handling category-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Creates a new category.
     *
     * @param category The category to create.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping
    public ResponseEntity<? extends BasicResponse> create(@RequestBody Category category) {
        return getResponse(() -> categoryService.create(category));
    }

    /**
     * Creates multiple categories.
     *
     * @param categories The list of categories to create.
     * @param ignored    Ignored parameter to differentiate from single category creation.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping(params = "multiple")
    public ResponseEntity<? extends BasicResponse> create(
            @RequestBody List<Category> categories,
            @RequestParam(name = "multiple") String ignored
    ) {
        return getResponse(() -> categoryService.create(categories));
    }

    /**
     * Retrieves all categories.
     *
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(categoryService::readAll);
    }

    /**
     * Retrieves a specific category by name.
     *
     * @param name The name of the category.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable String name) {
        return getResponse(() -> categoryService.read(name));
    }

    /**
     * Updates a category.
     *
     * @param name        The name of the category to update.
     * @param newCategory The new category data.
     * @return ResponseEntity with a BasicResponse.
     */
    @PatchMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> update(
            @PathVariable String name,
            @RequestBody Category newCategory
    ) {
        return getResponse(() -> categoryService.update(name, newCategory));
    }

    /**
     * Deletes a category.
     *
     * @param name The name of the category to delete.
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable String name) {
        return getResponse(() -> categoryService.delete(name));
    }

    /**
     * Deletes all categories.
     *
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping
    public ResponseEntity<? extends BasicResponse> deleteAll() {
        return getResponse(categoryService::deleteAll);
    }
}
