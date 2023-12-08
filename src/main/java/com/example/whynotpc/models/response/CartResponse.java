package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.OrderDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartResponse extends BasicResponse {
    @JsonProperty
    OrderDTO cart;

    public CartResponse(int statusCode, OrderDTO cart) {
        super(statusCode);
        this.cart = cart;
    }

    public CartResponse(int statusCode) {
        this(statusCode, null);
    }
}
