package com.example.whynotpc.models.order;

import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;

    private Integer quantity;

    @ManyToOne
    private Product product;

    private BigDecimal total;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    private Order order;

    @PostUpdate
    @PostPersist
    private void postPersistOrUpdate() {
        recalculateTotal();
    }

    @PreRemove
    private void preRemove() {
        if (order != null) {
            order.getItems().remove(this);
            order.recalculateTotal();
        }
    }

    public void recalculateTotal() {
        total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        if (order != null)
            order.recalculateTotal();
    }
}
