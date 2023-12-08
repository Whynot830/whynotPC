package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.ProductDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ProductResponse extends BasicResponse {
    @JsonProperty
    private final List<ProductDTO> products;

    public ProductResponse(int statusCode, List<ProductDTO> products) {
        super(statusCode);
        this.products = products;
    }
    public ProductResponse(int statusCode, ProductDTO product) {
        this(statusCode, Collections.singletonList(product));
    }
    public ProductResponse(int code) {
        this(code, Collections.emptyList());
    }
}
