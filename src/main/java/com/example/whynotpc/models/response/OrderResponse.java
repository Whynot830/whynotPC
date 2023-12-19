package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.OrderDTO;
import com.example.whynotpc.models.order.Order;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class OrderResponse extends BasicResponse {
    @JsonProperty
    List<OrderDTO> orders;

    private OrderDTO toDto(Order order) {
        return new OrderDTO(order.getId(), order.getStatus().name(),
                order.getTotal(), order.getItems(), order.getUser().getId());
    }

    public OrderResponse(int statusCode, List<Order> orders) {
        super(statusCode);
        this.orders = orders.stream().map(this::toDto).toList();
    }

    public OrderResponse(int statusCode, Order order) {
        super(statusCode);
        this.orders = Collections.singletonList(toDto(order));
    }

    public static OrderResponse ok(List<Order> orders) {
        return new OrderResponse(200, orders);
    }

    public static OrderResponse ok(Order order) {
        return new OrderResponse(200, order);
    }
}
