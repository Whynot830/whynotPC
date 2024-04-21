package com.example.whynotpc.models.product;

import com.example.whynotpc.models.order.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a product entity.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = "title")})
public class Product {
    /**
     * The unique identifier of the product.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The title of the product.
     */
    private String title;

    /**
     * The price of the product.
     */
    private BigDecimal price;

    /**
     * The name of the image associated with the product.
     */
    private String imgName;

    /**
     * The category to which the product belongs.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * The list of order items associated with this product. This relationship is excluded from toString method.
     */
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    /**
     * Recalculates the total amount of the order items associated with this product after an update.
     */
    @PostUpdate
    protected void postUpdate() {
        for (OrderItem orderItem : orderItems) {
            orderItem.recalculateTotal();
        }
    }
}
