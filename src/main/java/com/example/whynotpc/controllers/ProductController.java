package com.example.whynotpc.controllers;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.response.ProductResponse;
import com.example.whynotpc.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.whynotpc.utils.ResponseHandler.handleServiceCall;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ProductResponse> create(@RequestBody ProductDTO productDTO) {
        return handleServiceCall(() -> productService.create(productDTO));
    }

    @PostMapping("/all")
    public ResponseEntity<ProductResponse> create(@RequestBody List<ProductDTO> products) {
        return handleServiceCall(() -> productService.create(products));
    }

    @GetMapping
    public ResponseEntity<ProductResponse> readAll() {
        return handleServiceCall(productService::readAll);
    }

    @GetMapping(params = "category")
    public ResponseEntity<ProductResponse> readAllByCategory(@RequestParam String category) {
        return handleServiceCall(() -> productService.readAllByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> read(@PathVariable Integer id) {
        return handleServiceCall(() -> productService.read(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id, @RequestBody ProductDTO newProduct) {
        return handleServiceCall(() -> productService.update(id, newProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> delete(@PathVariable Integer id) {
        return handleServiceCall(() -> productService.delete(id));
    }

    @DeleteMapping()
    public ResponseEntity<ProductResponse> deleteAll() {
        return handleServiceCall(productService::deleteAll);
    }
}
