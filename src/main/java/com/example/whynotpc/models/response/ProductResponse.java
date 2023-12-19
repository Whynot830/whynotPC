package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ProductResponse extends BasicResponse {
    @JsonProperty
    private final List<ProductDTO> products;

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getTitle(), product.getPrice(),
                product.getImgName(), product.getCategory().getName());
    }

    public ProductResponse(int statusCode, List<Product> products) {
        super(statusCode);
        this.products = products.stream().map(this::toDTO).toList();
    }

    public ProductResponse(int statusCode, Product product) {
        super(statusCode);
        this.products = Collections.singletonList(toDTO(product));
    }

    public static ProductResponse ok(List<Product> products) {
        return new ProductResponse(200, products);
    }

    public static ProductResponse ok(Product product) {
        return new ProductResponse(200, product);
    }

    public static ProductResponse created(List<Product> products) {
        return new ProductResponse(201, products);
    }

    public static ProductResponse created(Product product) {
        return new ProductResponse(201, product);
    }
}
