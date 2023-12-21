package com.example.whynotpc.persistence.products;

import com.example.whynotpc.models.product.Category;
import com.example.whynotpc.models.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory(Category category);

    Page<Product> findPageByCategory(Category category, PageRequest pageRequest);
}
