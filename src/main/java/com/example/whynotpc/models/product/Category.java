package com.example.whynotpc.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Category {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
