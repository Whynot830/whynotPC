package com.example.whynotpc.models.order;

import com.example.whynotpc.models.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Float total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    public void recalculateTotal() {
        this.total = (float) items.stream().mapToDouble(OrderItem::getTotal).sum() * 100.0f / 100.0f;
    }
}
