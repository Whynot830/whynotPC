package com.example.whynotpc.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a category entity.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Category {
    /**
     * The unique identifier of the category.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The list of products belonging to this category. This relationship is excluded from toString method.
     */
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
