package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductPageResponse extends BasicResponse {
    @JsonProperty
    private final List<ProductDTO> products;
    @JsonProperty
    int totalPages;
    @JsonProperty
    int currentPage;

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getTitle(), product.getPrice(),
                product.getImgName(), product.getCategory().getName());
    }

    public ProductPageResponse(int statusCode, Page<Product> productPage) {
        super(statusCode);
        this.products = productPage.map(this::toDTO).toList();
        this.totalPages = productPage.getTotalPages();
        this.currentPage = productPage.getPageable().getPageNumber();
    }

    public ProductPageResponse(int statusCode, List<Product> products) {
        super(statusCode);
        this.products = products.stream().map(this::toDTO).toList();
        this.totalPages = 1;
        this.currentPage = 0;
    }

    public static ProductPageResponse ok(Page<Product> productPage) {
        return new ProductPageResponse(200, productPage);
    }

    public static ProductPageResponse ok(List<Product> products) {
        return new ProductPageResponse(200, products);
    }
}
