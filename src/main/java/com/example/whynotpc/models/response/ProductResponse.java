package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Represents a response for product-related operations.
 * Extends BasicResponse class.
 */
public class ProductResponse extends BasicResponse {
    /**
     * The list of product DTOs included in the response.
     */
    @JsonProperty
    private final List<ProductDTO> products;

    /**
     * Converts a Product object to its corresponding DTO representation.
     * @param product The Product object to convert.
     * @return ProductDTO representation of the Product object.
     */
    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getTitle(), product.getPrice(),
                product.getImgName(), product.getCategory().getName());
    }

    /**
     * Constructs a new ProductResponse with the given status code and list of products.
     * @param statusCode The HTTP status code of the response.
     * @param products list of products to include in the response.
     */
    public ProductResponse(int statusCode, List<Product> products) {
        super(statusCode);
        this.products = products.stream().map(this::toDTO).toList();
    }

    /**
     * Constructs a new ProductResponse with the given status code and product.
     * @param statusCode The HTTP status code of the response.
     * @param product product to include in the response.
     */
    public ProductResponse(int statusCode, Product product) {
        super(statusCode);
        this.products = Collections.singletonList(toDTO(product));
    }

    /**
     * Factory method to create a ProductResponse with HTTP status code 200 (OK) and the given list of products.
     * @param products The list of products to include in the response.
     * @return ProductResponse with status code 200 and the given list of products.
     */
    public static ProductResponse ok(List<Product> products) {
        return new ProductResponse(200, products);
    }

    /**
     * Factory method to create a ProductResponse with HTTP status code 200 (OK) and the given product.
     * @param product The product to include in the response.
     * @return ProductResponse with status code 200 and the given product.
     */
    public static ProductResponse ok(Product product) {
        return new ProductResponse(200, product);
    }

    /**
     * Factory method to create a ProductResponse with HTTP status code 201 (Created) and the given list of products.
     * @param products The list of products to include in the response.
     * @return ProductResponse with status code 201 and the given list of products.
     */
    public static ProductResponse created(List<Product> products) {
        return new ProductResponse(201, products);
    }

    /**
     * Factory method to create a ProductResponse with HTTP status code 201 (Created) and the given product.
     * @param product The product to include in the response.
     * @return ProductResponse with status code 201 and the given product.
     */
    public static ProductResponse created(Product product) {
        return new ProductResponse(201, product);
    }
}
