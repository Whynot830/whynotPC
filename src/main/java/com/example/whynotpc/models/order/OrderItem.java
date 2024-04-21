package com.example.whynotpc.models.order;

import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Represents an item in an order.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    /**
     * The unique identifier of the order item.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The quantity of the product in the order item.
     */
    private Integer quantity;

    /**
     * The product associated with the order item.
     */
    @ManyToOne
    private Product product;

    /**
     * The total price of the order item.
     */
    private BigDecimal total;

    /**
     * The order to which the order item belongs. This relationship is excluded from toString method.
     */
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    private Order order;

    /**
     * Calculates the total price of the order item.
     */
    @PostUpdate
    @PostPersist
    private void postPersistOrUpdate() {
        recalculateTotal();
    }

    /**
     * Removes the order item from its associated order and recalculates the total amount of the order.
     */
    @PreRemove
    private void preRemove() {
        if (order != null) {
            order.getItems().remove(this);
            order.recalculateTotal();
        }
    }

    /**
     * Recalculates the total price of the order item and updates the total amount of the associated order.
     */
    public void recalculateTotal() {
        total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (order != null)
            order.recalculateTotal();
    }
}
