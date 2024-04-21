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

/**
 * Controller class for handling product-related endpoints.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param productDTO The DTO containing product information.
     * @param file       The image file for the product (optional).
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping()
    public ResponseEntity<? extends BasicResponse> create(
            @RequestPart ProductDTO productDTO,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> productService.create(productDTO, file));
    }

    /**
     * Creates multiple products.
     *
     * @param products The list of product DTOs.
     * @param ignored  Ignored parameter to differentiate from single product creation.
     * @return ResponseEntity with a BasicResponse.
     */
    @PostMapping(params = "multiple")
    public ResponseEntity<? extends BasicResponse> create(
            @RequestBody List<ProductDTO> products,
            @RequestParam(name = "multiple") String ignored
    ) {
        return getResponse(() -> productService.create(products));
    }

    /**
     * Retrieves products based on optional query parameters.
     *
     * @param category The category of products to filter by (optional).
     * @param page     The page number for pagination (optional).
     * @param sort     The field to sort products by (optional).
     * @param order    The order for sorting (optional).
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping
    public ResponseEntity<? extends BasicResponse> read(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order
    ) {
        return getResponse(() -> productService.read(category, page, sort, order));
    }

    /**
     * Retrieves a specific product by ID.
     *
     * @param id The ID of the product.
     * @return ResponseEntity with a BasicResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> read(@PathVariable Long id) {
        return getResponse(() -> productService.read(id));
    }

    /**
     * Updates a product.
     *
     * @param id         The ID of the product to update.
     * @param newProduct The new product data (optional).
     * @param file       The new image file for the product (optional).
     * @return ResponseEntity with a BasicResponse.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> update(
            @PathVariable Long id,
            @RequestPart(required = false) ProductDTO newProduct,
            @RequestPart(required = false) MultipartFile file
    ) {
        return getResponse(() -> productService.update(id, newProduct, file));
    }

    /**
     * Deletes a specific product by ID.
     *
     * @param id The ID of the product to delete.
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable Long id) {
        return getResponse(() -> productService.delete(id));
    }

    /**
     * Deletes all products.
     *
     * @return ResponseEntity with a BasicResponse.
     */
    @DeleteMapping()
    public ResponseEntity<? extends BasicResponse> deleteAll() {
        return getResponse(productService::deleteAll);
    }
}
