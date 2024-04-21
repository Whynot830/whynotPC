package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Represents a response for product page-related operations.
 * Extends BasicResponse class.
 */
public class ProductPageResponse extends BasicResponse {
    /**
     * The list of product DTOs included in the response.
     */
    @JsonProperty
    private final List<ProductDTO> products;

    /**
     * The total number of pages in the product page.
     */
    @JsonProperty
    int totalPages;

    /**
     * The current page number in the product page.
     */
    @JsonProperty
    int currentPage;

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
     * Constructs a new ProductPageResponse with the given status code and product page.
     * @param statusCode The HTTP status code of the response.
     * @param productPage The product page to include in the response.
     */
    public ProductPageResponse(int statusCode, Page<Product> productPage) {
        super(statusCode);
        this.products = productPage.map(this::toDTO).toList();
        this.totalPages = productPage.getTotalPages();
        this.currentPage = productPage.getPageable().getPageNumber();
    }

    /**
     * Constructs a new ProductPageResponse with the given status code and list of products.
     * @param statusCode The HTTP status code of the response.
     * @param products The list of products to include in the response.
     */
    public ProductPageResponse(int statusCode, List<Product> products) {
        super(statusCode);
        this.products = products.stream().map(this::toDTO).toList();
        this.totalPages = 1;
        this.currentPage = 0;
    }

    /**
     * Factory method to create a ProductPageResponse with HTTP status code 200 (OK) and the given product page.
     * @param productPage The product page to include in the response.
     * @return ProductPageResponse with status code 200 and the given product page.
     */
    public static ProductPageResponse ok(Page<Product> productPage) {
        return new ProductPageResponse(200, productPage);
    }

    /**
     * Factory method to create a ProductPageResponse with HTTP status code 200 (OK) and the given list of products.
     * @param products The list of products to include in the response.
     * @return ProductPageResponse with status code 200 and the given list of products.
     */
    public static ProductPageResponse ok(List<Product> products) {
        return new ProductPageResponse(200, products);
    }
}
