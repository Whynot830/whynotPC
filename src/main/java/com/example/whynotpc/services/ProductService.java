package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.example.whynotpc.models.response.BasicResponse;
import com.example.whynotpc.models.response.ProductResponse;
import com.example.whynotpc.persistence.orders.OrderItemRepo;
import com.example.whynotpc.persistence.products.CategoryRepo;
import com.example.whynotpc.persistence.products.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.whynotpc.models.response.BasicResponse.noContent;
import static com.example.whynotpc.models.response.ProductResponse.created;
import static com.example.whynotpc.models.response.ProductResponse.ok;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final OrderItemRepo orderItemRepo;

    private Product save(ProductDTO productDTO) {
        if (isNullOrBlank(productDTO.title()) || isNullOrBlank(productDTO.category()) || productDTO.price() == null)
            throw new IllegalArgumentException("Some properties are null or blank");

        if (productDTO.price().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be a non-negative value");

        var category = categoryRepo
                .findByName(productDTO.category())
                .orElseThrow(() -> new IllegalArgumentException("Category with given name not found"));

        return productRepo.save(Product.builder()
                .title(productDTO.title())
                .price(productDTO.price())
                .imgName(productDTO.imgName())
                .category(category)
                .build());
    }

    public ProductResponse create(ProductDTO productDTO) {
        var product = save(productDTO);
        return ok(product);
    }

    @Transactional
    public ProductResponse create(List<ProductDTO> products) {
        List<Product> savedProducts = new ArrayList<>();
        Product savedProduct;
        for (var product : products) {
            savedProduct = save(product);
            savedProducts.add(savedProduct);
        }
        return created(savedProducts);
    }

    public ProductResponse readAll() {
        return ProductResponse.ok(productRepo.findAll());
    }

    public ResponseEntity<Page<Product>> readPageable(Integer page) {
        Page<Product> productPage = productRepo.findAll(PageRequest.of(0, 16));
        System.out.println(page);

        System.out.println(productPage.getTotalPages());
        return ResponseEntity.ok(productPage);
    }

    public ProductResponse readAllByCategory(String categoryName) {
        var category = categoryRepo.findByName(categoryName).orElseThrow(EntityNotFoundException::new);
        return ok(productRepo.findAllByCategory(category));
    }

    public ProductResponse read(Long id) {
        return productRepo.findById(id)
                .map(ProductResponse::ok)
                .orElseThrow(EntityNotFoundException::new);
    }

    public ProductResponse update(Long id, ProductDTO newProduct) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        var category = categoryRepo.findByName(newProduct.category()).orElse(null);

        if (newProduct.category() != null && category == null)
            throw new IllegalArgumentException("Category with given name not found");
        if (newProduct.price() != null && newProduct.price().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be a non-negative value");

        if (!isNullOrBlank(newProduct.title())) product.setTitle(newProduct.title());
        if (newProduct.price() != null) product.setPrice(newProduct.price());
        if (!isNullOrBlank(newProduct.imgName())) product.setImgName(newProduct.imgName());
        if (!isNullOrBlank(newProduct.category())) product.setCategory(category);

        var items = product.getOrderItems();
        orderItemRepo.saveAll(items);
        product = productRepo.save(product);

        return ok(product);
    }

    public BasicResponse delete(Long id) {
        var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        productRepo.delete(product);
        return noContent();
    }


    public BasicResponse deleteAll() {
        productRepo.deleteAll();
        return noContent();
    }
}
