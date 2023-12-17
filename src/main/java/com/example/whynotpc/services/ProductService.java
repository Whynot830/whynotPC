package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.example.whynotpc.models.response.ProductResponse;
import com.example.whynotpc.persistence.orders.OrderItemRepo;
import com.example.whynotpc.persistence.products.CategoryRepo;
import com.example.whynotpc.persistence.products.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.whynotpc.utils.JPACallHandler.Result;
import static com.example.whynotpc.utils.JPACallHandler.handleCall;
import static com.example.whynotpc.utils.StrChecker.isNullOrBlank;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final OrderItemRepo orderItemRepo;

    private Result<Product> save(ProductDTO productDTO) {
        return handleCall(() -> {
            if (isNullOrBlank(productDTO.title()) || isNullOrBlank(productDTO.category()) || productDTO.price() == null)
                throw new IllegalArgumentException("Some properties are null or blank");

            if (productDTO.price().compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Price must be a non-negative value");

            var category = categoryRepo.findByName(productDTO.category()).orElseThrow(IllegalArgumentException::new);
            return productRepo.save(Product.builder()
                    .title(productDTO.title())
                    .price(productDTO.price())
                    .imgName(productDTO.imgName())
                    .category(category)
                    .build());
        });
    }

    public ProductResponse create(ProductDTO productDTO) {
        var response = save(productDTO);
        return switch (response.statusCode()) {
            case 200 -> new ProductResponse(201, response.result());
            case 400 -> new ProductResponse(400);
            case 409 -> new ProductResponse(409);
            default -> new ProductResponse(500);
        };
    }

    @Transactional
    public ProductResponse create(List<ProductDTO> products) {
        List<Product> savedProducts = new ArrayList<>();
        Result<Product> response;
        for (var product : products) {
            if (productRepo.existsByTitle(product.title())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ProductResponse(409);
            }
            response = save(product);
            if (response.result() == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ProductResponse(response.statusCode());
            }
            savedProducts.add(response.result());
        }
        return new ProductResponse(201, savedProducts);
    }

    public ProductResponse readAll() {
        Page<Product> products = productRepo.findAll(PageRequest.of(0, 16));
        return new ProductResponse(200, productRepo.findAll());
    }

    public ProductResponse readAllByCategory(String categoryName) {
        return categoryRepo.findByName(categoryName)
                .map(category -> new ProductResponse(
                        200,
                        productRepo.findAllByCategory(category))
                )
                .orElse(new ProductResponse(400));
    }

    public ProductResponse read(Integer id) {
        return productRepo.findById(id)
                .map(product -> new ProductResponse(200, product))
                .orElse(new ProductResponse(404));
    }

    public ProductResponse update(Integer id, ProductDTO newProduct) {
        var response = handleCall(() -> {
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
            return productRepo.save(product);
        });
        return switch (response.statusCode()) {
            case 200 -> new ProductResponse(200, response.result());
            case 400 -> new ProductResponse(400);
            case 404 -> new ProductResponse(404);
            default -> new ProductResponse(500);
        };
    }

    public ProductResponse delete(Integer id) {
        var response = handleCall(() -> productRepo.findById(id)
                .map(product -> {
                    productRepo.delete(product);
                    return true;
                })
                .orElseThrow(EntityNotFoundException::new));
        return switch (response.statusCode()) {
            case 200 -> new ProductResponse(204);
            case 404 -> new ProductResponse(404);
            default -> new ProductResponse(500);
        };
    }

    public ProductResponse deleteAll() {
        productRepo.deleteAll();
        return new ProductResponse(204);
    }
}
