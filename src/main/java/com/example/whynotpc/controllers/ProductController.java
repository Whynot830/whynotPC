package com.example.whynotpc.controllers;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.whynotpc.utils.ServiceCallHandler.getResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<? extends BasicResponse> create(
            @RequestPart ProductDTO productDTO,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> productService.create(productDTO, file));
    }

    @PostMapping(params = "multiple")
    public ResponseEntity<? extends BasicResponse> create(
            @RequestBody List<ProductDTO> products,
            @RequestParam(name = "multiple") String ignored
    ) {
        return getResponse(() -> productService.create(products));
    }

    @GetMapping
    public ResponseEntity<? extends BasicResponse> readAll() {
        return getResponse(productService::readAll);
    }

    @GetMapping(params = "p")
    public ResponseEntity<?> readSorting(@RequestParam Integer p) {
        return productService.readPageable(p);
//        return handleServiceCall(() -> productService.readPageable(p));
    }

    @GetMapping(params = "category")
    public ResponseEntity<? extends BasicResponse> readAllByCategory(@RequestParam String category) {
        return getResponse(() -> productService.readAllByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable Long id) {
        return getResponse(() -> productService.read(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> update(
            @PathVariable Long id,
            @RequestPart(required = false) ProductDTO newProduct,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> productService.update(id, newProduct, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        return getResponse(() -> productService.delete(id));
    }

    @DeleteMapping()
    public ResponseEntity<? extends BasicResponse> deleteAll() {
        return getResponse(productService::deleteAll);
    }
}
