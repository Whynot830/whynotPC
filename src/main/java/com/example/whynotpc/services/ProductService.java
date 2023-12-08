package com.example.whynotpc.services;

import com.example.whynotpc.models.dto.ProductDTO;
import com.example.whynotpc.models.product.Product;
import com.example.whynotpc.models.response.ProductResponse;
import com.example.whynotpc.persistence.products.CategoryRepo;
import com.example.whynotpc.persistence.products.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getTitle(), product.getPrice(),
                product.getImgName(), product.getCategory().getName());
    }

    public Result<Product> save(ProductDTO productDTO) {
        return handleCall(() -> {
            if (isNullOrBlank(productDTO.title()) || isNullOrBlank(productDTO.category()))
                throw new IllegalArgumentException("Some properties are null or blank");

            if (productDTO.price() < 0)
                throw new IllegalArgumentException("Price must be non-negative");

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
            case 200 -> new ProductResponse(201, toDTO(response.result()));
            case 400 -> new ProductResponse(400);
            default -> new ProductResponse(500);
        };
    }

    @Transactional
    public ProductResponse create(List<ProductDTO> products) {
        List<Product> savedProducts = new ArrayList<>();
        Result<Product> response;
        for (var product : products) {
            response = save(product);
            if (response.result() == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ProductResponse(400);
            }
            savedProducts.add(response.result());
        }
        return new ProductResponse(201, savedProducts.stream().map(this::toDTO).toList());
    }

    public ProductResponse readAll() {
        return new ProductResponse(200, productRepo.findAll().stream().map(this::toDTO).toList());
    }

    public ProductResponse readAllByCategory(String categoryName) {
        return categoryRepo.findByName(categoryName)
                .map(category -> new ProductResponse(
                        200,
                        productRepo.findAllByCategory(category).stream()
                                .map(this::toDTO)
                                .toList())
                )
                .orElse(new ProductResponse(400));
    }

    public ProductResponse read(Integer id) {
        return productRepo.findById(id)
                .map(product -> new ProductResponse(200, toDTO(product)))
                .orElse(new ProductResponse(404));
    }

    @Transactional
    public ProductResponse update(Integer id, ProductDTO newProduct) {
        var response = handleCall(() -> {
            var product = productRepo.findById(id).orElseThrow(EntityNotFoundException::new);
            var category = categoryRepo.findByName(newProduct.category()).orElse(null);
            if (newProduct.category() != null && category == null || newProduct.price() != null && newProduct.price() <= 0)
                throw new IllegalArgumentException("Category with given name not found");

            if (!isNullOrBlank(newProduct.title())) product.setTitle(newProduct.title());
            if (newProduct.price() != null) product.setPrice(newProduct.price());
            if (!isNullOrBlank(newProduct.imgName())) product.setImgName(newProduct.imgName());
            if (!isNullOrBlank(newProduct.category())) product.setCategory(category);
            return product;
        });
        return switch (response.statusCode()) {
            case 200 -> new ProductResponse(200, toDTO(response.result()));
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
