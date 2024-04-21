package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.OrderDTO;
import com.example.whynotpc.models.order.Order;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Represents a response for order-related operations.
 * Extends BasicResponse class.
 */
public class OrderResponse extends BasicResponse {
    /**
     * The list of order DTOs included in the response.
     */
    @JsonProperty
    List<OrderDTO> orders;

    /**
     * Converts an Order object to its corresponding DTO representation.
     * @param order The Order object to convert.
     * @return OrderDTO representation of the Order object.
     */
    private OrderDTO toDto(Order order) {
        return new OrderDTO(order.getId(), order.getStatus().name(),
                order.getTotal(), order.getItems(), order.getUser().getId());
    }

    /**
     * Constructs a new OrderResponse with the given status code and list of orders.
     * @param statusCode The HTTP status code of the response.
     * @param orders The list of orders to include in the response.
     */
    public OrderResponse(int statusCode, List<Order> orders) {
        super(statusCode);
        this.orders = orders.stream().map(this::toDto).toList();
    }

    /**
     * Constructs a new OrderResponse with the given status code and single order.
     * @param statusCode The HTTP status code of the response.
     * @param order The order to include in the response.
     */
    public OrderResponse(int statusCode, Order order) {
        super(statusCode);
        this.orders = Collections.singletonList(toDto(order));
    }

    /**
     * Factory method to create an OrderResponse with HTTP status code 200 (OK) and the given list of orders.
     * @param orders The list of orders to include in the response.
     * @return OrderResponse with status code 200 and the given list of orders.
     */
    public static OrderResponse ok(List<Order> orders) {
        return new OrderResponse(200, orders);
    }

    /**
     * Factory method to create an OrderResponse with HTTP status code 200 (OK) and the given order.
     * @param order The order to include in the response.
     * @return OrderResponse with status code 200 and the given order.
     */
    public static OrderResponse ok(Order order) {
        return new OrderResponse(200, order);
    }
}
