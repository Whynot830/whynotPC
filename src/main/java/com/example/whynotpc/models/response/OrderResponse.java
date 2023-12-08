package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.OrderDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class OrderResponse extends BasicResponse {
    @JsonProperty
    List<OrderDTO> orders;

    public OrderResponse(int statusCode, List<OrderDTO> orders) {
        super(statusCode);
        this.orders = orders;
    }
    public OrderResponse(int statusCode, OrderDTO order) {
        this(statusCode, Collections.singletonList(order));
    }
    public OrderResponse(int statusCode) {
        this(statusCode, Collections.emptyList());
    }
}
