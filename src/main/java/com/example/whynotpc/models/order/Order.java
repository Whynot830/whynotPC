package com.example.whynotpc.models.order;

import com.example.whynotpc.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Represents an order entity.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    /**
     * The unique identifier of the order.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The status of the order.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * The total amount of the order.
     */
    private BigDecimal total;

    /**
     * The list of items in the order. This relationship is mapped by the 'order' field in the OrderItem class.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    /**
     * The user who placed the order. This relationship is excluded from toString method.
     */
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * Recalculates the total amount of the order based on the items.
     */
    public void recalculateTotal() {
        total = items.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
