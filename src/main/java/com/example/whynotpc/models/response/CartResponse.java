package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.OrderDTO;
import com.example.whynotpc.models.order.Order;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CartResponse extends BasicResponse {
    @JsonProperty
    OrderDTO cart;

    private OrderDTO toDto(Order order) {
        return new OrderDTO(order.getId(), order.getStatus().name(),
                order.getTotal(), order.getItems(), order.getUser().getId());
    }

    public CartResponse(int statusCode, Order cart) {
        super(statusCode);
        this.cart = toDto(cart);
    }

    public CartResponse(int statusCode) {
        super(statusCode);
        this.cart = null;
    }
}
