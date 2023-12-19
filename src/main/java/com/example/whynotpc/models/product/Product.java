package com.example.whynotpc.models.product;

import com.example.whynotpc.models.order.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = "title")})
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private BigDecimal price;

    private String imgName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @PostUpdate
    protected void postUpdate() {
        for (OrderItem orderItem : orderItems) {
            orderItem.recalculateTotal();
        }
    }
}
