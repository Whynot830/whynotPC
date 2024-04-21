package com.example.whynotpc.models.response;

import com.example.whynotpc.models.dto.OrderDTO;
import com.example.whynotpc.models.order.Order;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a response for cart-related operations.
 * Extends BasicResponse class.
 */
public class CartResponse extends BasicResponse {
    /**
     * The DTO representing the cart.
     */
    @JsonProperty
    OrderDTO cart;

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
     * Constructs a new CartResponse with the given status code and cart.
     * @param statusCode The HTTP status code of the response.
     * @param cart The cart to include in the response.
     */
    public CartResponse(int statusCode, Order cart) {
        super(statusCode);
        this.cart = toDto(cart);
    }

    /**
     * Factory method to create a CartResponse with HTTP status code 200 (OK) and the given cart.
     * @param cart The cart to include in the response.
     * @return CartResponse with status code 200 and the given cart.
     */
    public static CartResponse ok(Order cart) {
        return new CartResponse(200, cart);
    }
}
