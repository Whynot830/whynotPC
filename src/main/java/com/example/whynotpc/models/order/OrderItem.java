package com.example.whynotpc.models.order;

import com.example.whynotpc.models.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer quantity;
    @ManyToOne
    private Product product;
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    private Order order;

    public Float getTotal() {
        return quantity * product.getPrice() * 100.0f / 100.0f;
    }

    @PostUpdate
    @PostPersist
    private void postPersistOrRemove() {
        if (order != null) {
            order.recalculateTotal();
        }
    }
    @PreRemove
    private void preRemove() {
        if (order != null) {
            order.getItems().remove(this);
            order.recalculateTotal();
        }
    }
}
